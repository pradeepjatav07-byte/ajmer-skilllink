package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, String> {
    List<ProviderProfile> findAllByActiveTrueOrderByTrustScoreDesc();
    List<ProviderProfile> findAllByVerificationStatusOrderByCreatedAtAsc(String status);
    List<ProviderProfile> findAllByPrimaryCategory_SlugAndActiveTrue(String slug);
    long countByVerificationStatus(String status);
}
