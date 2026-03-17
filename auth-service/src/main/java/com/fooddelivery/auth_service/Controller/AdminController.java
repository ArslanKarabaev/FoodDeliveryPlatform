package com.fooddelivery.auth_service.Controller;

import com.fooddelivery.auth_service.Dto.CreateCafeAdminRequest;
import com.fooddelivery.auth_service.Entity.User;
import com.fooddelivery.auth_service.Repository.UserRepository;
import com.fooddelivery.auth_service.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/users/cafe-admin")
    public ResponseEntity<String> createCafeAdmin(@Valid @RequestBody CreateCafeAdminRequest request){
        authService.createCafeAdmin(request);
        return ResponseEntity.ok("Кафе-админ создан");
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable UUID id,
            @PathVariable Boolean active){
        authService.updateUserStatus(id, active);
        return ResponseEntity.ok("Статус обновлён");
    }

    @GetMapping("/users/{id}/email")
    public ResponseEntity<String> getUserEmail(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return ResponseEntity.ok(user.getEmail());
    }
}
