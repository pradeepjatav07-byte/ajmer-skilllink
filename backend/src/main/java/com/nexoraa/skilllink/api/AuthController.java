package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.UserEntity;
import com.nexoraa.skilllink.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.AuthResponse register(@Valid @RequestBody ApiDtos.RegisterRequest request) { return authService.register(request); }

    @PostMapping("/api/auth/login")
    public ApiDtos.AuthResponse login(@Valid @RequestBody ApiDtos.LoginRequest request) { return authService.login(request); }

    @PostMapping("/api/auth/refresh")
    public ApiDtos.AuthResponse refresh(@Valid @RequestBody ApiDtos.RefreshRequest request) { return authService.refresh(request.refreshToken()); }

    @PostMapping("/api/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody ApiDtos.LogoutRequest request) { authService.logout(request.refreshToken()); }

    @GetMapping("/api/users/me")
    public ApiDtos.UserView me(@AuthenticationPrincipal UserEntity user) { return ApiDtos.UserView.from(user); }
}
