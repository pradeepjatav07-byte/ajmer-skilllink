package com.nexoraa.skilllink.service;

import com.nexoraa.skilllink.api.ApiDtos;
import com.nexoraa.skilllink.domain.*;
import com.nexoraa.skilllink.repository.*;
import com.nexoraa.skilllink.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final ProviderProfileRepository providers;
    private final ServiceCategoryRepository categories;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${app.jwt.refresh-days:30}") private long refreshDays;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public ApiDtos.AuthResponse register(ApiDtos.RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        Role role = request.role() == ApiDtos.PublicRole.PROVIDER ? Role.PROVIDER : Role.CUSTOMER;
        UserEntity user = users.save(UserEntity.builder().fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase()).passwordHash(passwordEncoder.encode(request.password()))
                .role(role).status("ACTIVE").build());
        if (role == Role.PROVIDER) {
    providers.save(ProviderProfile.builder().user(user)
            .providerCode("ASL-" + user.getId().substring(0, 8).toUpperCase())
            .primaryArea("")
            .experienceYears(0)
            .skillsJson("[]")
            .aboutText(null)
            .verificationStatus("NOT_SUBMITTED")
            .trustScore(0)
            .ratingAverage(0)
            .completedJobs(0)
            .emergencyAvailable(false)
            .availableToday(false)
            .active(false)
            .build());
        }
        return issuePair(user);
    }

    @Transactional
    public ApiDtos.AuthResponse login(ApiDtos.LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        UserEntity user = users.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        if (!user.isEnabled() || !user.isAccountNonLocked()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account is not active.");
        refreshTokens.deleteAllByUser_Id(user.getId());
        return issuePair(user);
    }

    @Transactional
    public ApiDtos.AuthResponse refresh(String rawToken) {
        String hash = hashToken(rawToken);
        RefreshToken token = refreshTokens.findByTokenHash(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is invalid or expired."));
        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokens.delete(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is invalid or expired.");
        }
        UserEntity user = token.getUser();
        refreshTokens.delete(token);
        return issuePair(user);
    }

    @Transactional
    public void logout(String rawToken) {
        refreshTokens.findByTokenHash(hashToken(rawToken)).ifPresent(refreshTokens::delete);
    }

    private ApiDtos.AuthResponse issuePair(UserEntity user) {
        byte[] bytes = new byte[48]; random.nextBytes(bytes);
        String rawRefresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokens.save(RefreshToken.builder().user(user).tokenHash(hashToken(rawRefresh))
                .expiresAt(Instant.now().plusSeconds(refreshDays * 86400)).build());
        return new ApiDtos.AuthResponse(jwtService.createAccessToken(user), rawRefresh, "Bearer", jwtService.accessSeconds(), ApiDtos.UserView.from(user));
    }

    private String hashToken(String raw) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException("Could not hash refresh token", e); }
    }
}
