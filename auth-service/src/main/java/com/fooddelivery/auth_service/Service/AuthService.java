package com.fooddelivery.auth_service.Service;

import com.fooddelivery.auth_service.Dto.*;
import com.fooddelivery.auth_service.Entity.User;
import com.fooddelivery.auth_service.Enums.Role;
import com.fooddelivery.auth_service.Mapper.UserMapper;
import com.fooddelivery.auth_service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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

 /*  ВОЗМОЖНО БУДЕТ НЕ НУЖЕН

        public void register(RegisterRequest request){
        if(request.getEmail() != null && userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email уже занят");
        }
        if (userRepository.existsByPhone(request.getPhone())){
            throw new RuntimeException("Телефон уже занят");
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRole(Role.CLIENT);
        if(request.getName() != null && !request.getName().isBlank()){
            user.setName(request.getName());
        }

        userRepository.save(user);

        boolean needsUpdate = false;
        if(user.getName() == null || user.getName().isBlank()){
            String shortId = user.getId().toString().substring(0, 8);
            user.setName("User #" + shortId);
            needsUpdate = true;
        }

        if (needsUpdate){
            userRepository.save(user);
        }

    }*/

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isActive()) {
            throw new RuntimeException("Аккаунт деактивирован");
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
                .forcePasswordChange(user.isForcePasswordChange())
                .build();
    }

    public void logout(String accessToken) {
        if (!jwtService.isTokenValid(accessToken)) {
            throw new BadCredentialsException("Invalid token");
        }

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

        UUID userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId)
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

        if((request.getEmail() == null || request.getEmail().isBlank()) && (request.getPhone() == null || request.getPhone().isBlank())){
            throw new RuntimeException("Заполните email или номер телефона");
        }

        User user;
        if(request.getEmail() != null && !request.getEmail().isBlank()) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        } else {
            user = userRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        }

        String resetToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                "reset:" + resetToken,
                user.getId().toString(),
                1,
                TimeUnit.HOURS
        );

        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

        Map<String, String> event = new HashMap<>();
        event.put("clientId", user.getId().toString());
        event.put("resetLink", resetLink);
        if (user.getEmail() != null) {
            event.put("email", user.getEmail());
        }
        if (user.getPhone() != null) {
            event.put("phone", user.getPhone());
        }
        rabbitTemplate.convertAndSend("password.reset", event);
    }

    public void resetPassword(ResetPasswordRequest request) {
        String userId = redisTemplate.opsForValue().get("reset:" + request.getToken());

        if (userId == null) {
            throw new RuntimeException("Токен недействителен или истёк");
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setForcePasswordChange(false);
        userRepository.save(user);

        redisTemplate.delete("reset:" + request.getToken());
    }

    // регистрация юсеров через телефон и код подтвержения
    public void sendOtp(SendOtpRequest request) {
        String phone = request.getPhone();

        // Защита от спама — если код уже отправлен и не истёк, не шлём снова
        Boolean alreadySent = redisTemplate.hasKey("otp:" + phone);
        if (Boolean.TRUE.equals(alreadySent)) {
            throw new RuntimeException("Код уже отправлен. Подождите перед повторной отправкой");
        }

        // Генерируем 4-значный код
        String code = String.format("%04d", new java.util.Random().nextInt(10000));

        // Сохраняем в Redis на 5 минут
        redisTemplate.opsForValue().set(
                "otp:" + phone,
                code,
                5,
                TimeUnit.MINUTES
        );

        // Отправляем через RabbitMQ → Notification Service
        Map<String, String> event = new HashMap<>();
        event.put("phone", phone);
        event.put("code", code);
        rabbitTemplate.convertAndSend("otp.send", event);
    }

    // Шаг 2 — проверить OTP и выдать JWT
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();

        // Достаём код из Redis
        String savedCode = redisTemplate.opsForValue().get("otp:" + phone);

        if (savedCode == null) {
            throw new RuntimeException("Код истёк или не был отправлен");
        }
        if (!savedCode.equals(code)) {
            throw new RuntimeException("Неверный код");
        }

        // Код верный — удаляем из Redis
        redisTemplate.delete("otp:" + phone);

        // Ищем юзера или создаём нового (авторегистрация)
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> createClientByPhone(phone));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .userId(user.getId())
                .forcePasswordChange(false)
                .build();
    }

    // Вспомогательный — создаём клиента при первом входе
    private User createClientByPhone(String phone) {
        User user = User.builder()
                .phone(phone)
                .role(Role.CLIENT)
                .isActive(true)
                .forcePasswordChange(false)
                .build();

        userRepository.save(user);

        // Дефолтное имя после получения ID
        String shortId = user.getId().toString().substring(0, 8);
        user.setName("User#" + shortId);
        userRepository.save(user);

        return user;
    }

}
