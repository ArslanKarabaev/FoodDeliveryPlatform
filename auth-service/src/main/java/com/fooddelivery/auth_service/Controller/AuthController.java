package com.fooddelivery.auth_service.Controller;

import com.fooddelivery.auth_service.Dto.AuthResponse;
import com.fooddelivery.auth_service.Dto.LoginRequest;
import com.fooddelivery.auth_service.Dto.RefreshRequest;
import com.fooddelivery.auth_service.Dto.RegisterRequest;
import com.fooddelivery.auth_service.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
    authService.register(request);
    return ResponseEntity.ok("Регистрация успешна");
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


}
