package com.nexoraa.skilllink.security;

import com.nexoraa.skilllink.domain.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long accessMinutes;

    public JwtService(@Value("${app.jwt.secret-base64}") String secretBase64,
                      @Value("${app.jwt.access-minutes:20}") long accessMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.accessMinutes = accessMinutes;
    }

    public String createAccessToken(UserEntity user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getEmail())
                .claim("uid", user.getId()).claim("role", user.getRole().name())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(accessMinutes * 60)))
                .signWith(signingKey).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }
    public long accessSeconds() { return accessMinutes * 60; }
}
