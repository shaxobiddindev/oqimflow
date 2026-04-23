package uz.transport.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.transport.monitoring.dto.ApiResponse;
import uz.transport.monitoring.dto.AuthDto;
import uz.transport.monitoring.service.AuthService;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autentifikatsiya endpointlari")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Tizimga kirish", description = "Username va parol bilan login qilish")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/register")
    @Operation(summary = "Yangi foydalanuvchi qo'shish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Foydalanuvchi yaratildi", authService.register(request)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Token yangilash")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> refresh(
            @Valid @RequestBody AuthDto.RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request)));
    }
}
