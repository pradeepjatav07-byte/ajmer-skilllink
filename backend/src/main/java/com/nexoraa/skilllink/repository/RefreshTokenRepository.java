package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteAllByUser_Id(String userId);
}
