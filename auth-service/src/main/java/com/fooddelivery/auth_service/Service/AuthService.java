package com.fooddelivery.auth_service.Service;

import com.fooddelivery.auth_service.Dto.*;
import com.fooddelivery.auth_service.Entity.User;
import com.fooddelivery.auth_service.Enums.Role;
import com.fooddelivery.auth_service.Mapper.UserMapper;
import com.fooddelivery.auth_service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public void register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email уже занят");
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())){
            throw new RuntimeException("Телефон уже занят");
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRole(Role.CLIENT);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isActive()) {
            throw new RuntimeException("account deactivated Аккаунт деактивирован");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Неверный пароль");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    public void logout(String accessToken) {
        long ttl = jwtService.getTimeUntilExpire(accessToken);
        redisTemplate.opsForValue().set(
                "blacklist:" + accessToken,
                "true",
                ttl,
                TimeUnit.MILLISECONDS
        );
    }

    public AuthResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Невалидный refresh token");
        }

        Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + token);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            throw new RuntimeException("Токен инвалидирован");
        }

        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    public void createCafeAdmin(CreateCafeAdminRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email уже используется");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getTemporaryPassword()))
                .role(Role.CAFE_ADMIN)
                .cafeId(request.getCafeId())
                .forcePasswordChange(true)
                .build();

        userRepository.save(user);
    }

    public void updateUserStatus(UUID userId, boolean active){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("Пользователь не найден"));
        user.setActive(active);
        userRepository.save(user);
    }

    public void forgotPassword(ForgotPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("Пользователь не найден"));

        String resetToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                "reset:" + resetToken,
                user.getEmail(),
                1,
                TimeUnit.HOURS
        );

        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

        rabbitTemplate.convertAndSend("password.reset",
                user.getEmail() + ":" + resetLink);
    }

    public void resetPassword(ResetPasswordRequest request) {
        // Проверяем токен в Redis
        String email = (String) redisTemplate.opsForValue().get("reset:" + request.getToken());

        if (email == null) {
            throw new RuntimeException("Токен недействителен или истёк");
        }

        // Находим пользователя
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Меняем пароль
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Удаляем токен из Redis
        redisTemplate.delete("reset:" + request.getToken());
    }

}
