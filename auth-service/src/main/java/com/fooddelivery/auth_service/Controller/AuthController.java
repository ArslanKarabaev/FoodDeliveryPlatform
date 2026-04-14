package com.fooddelivery.auth_service.Controller;

import com.fooddelivery.auth_service.Dto.*;
import com.fooddelivery.auth_service.Service.AuthService;
import com.fooddelivery.auth_service.Service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok("Код отправлен на номер " + request.getPhone());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok("Выход выполнен");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {

        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("Инструкции по сбросу пароля отправлены");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Пароль изменён");
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        UUID userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(authService.getProfile(userId));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {
        String token = authHeader.substring(7);
        UUID userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(authService.updateProfile(userId, request));
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<ProfileResponse> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        String token = authHeader.substring(7);
        UUID userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(authService.updateAvatar(userId, file));
    }


}
