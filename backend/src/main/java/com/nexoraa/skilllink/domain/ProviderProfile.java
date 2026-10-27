package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "provider_profiles", indexes = {
        @Index(name = "idx_provider_area", columnList = "primary_area"),
        @Index(name = "idx_provider_verification", columnList = "verification_status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProviderProfile {
    @Id @Column(name = "user_id", length = 36) private String id;
    @OneToOne(fetch = FetchType.LAZY) @MapsId @JoinColumn(name = "user_id") private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "primary_category_id") private ServiceCategory primaryCategory;
    @Column(name = "provider_code", nullable = false, unique = true, length = 24) private String providerCode;
    @Column(name = "primary_area", nullable = false, length = 80) private String primaryArea;
    @Column(name = "experience_years", nullable = false) private int experienceYears;
    @Column(name = "skills_json", columnDefinition = "json") private String skillsJson;
    @Column(name = "about_text", length = 1200) private String aboutText;
    @Column(name = "languages", length = 255) private String languages;
    @Column(name = "verification_status", nullable = false, length = 24) @Builder.Default private String verificationStatus = "UNDER_REVIEW";
    @Column(name = "rating_average", nullable = false) @Builder.Default private double ratingAverage = 0.0;
    @Column(name = "completed_jobs", nullable = false) @Builder.Default private int completedJobs = 0;
    @Column(name = "trust_score", nullable = false) @Builder.Default private int trustScore = 50;
    @Column(name = "on_time_rate", nullable = false) @Builder.Default private double onTimeRate = 0.0;
    @Column(name = "on_time_sample_count", nullable = false) @Builder.Default private int onTimeSampleCount = 0;
    @Column(name = "cancellation_rate", nullable = false) @Builder.Default private double cancellationRate = 0.0;
    @Column(name = "emergency_available", nullable = false) @Builder.Default private boolean emergencyAvailable = false;
    @Column(name = "female_professional", nullable = false) @Builder.Default private boolean femaleProfessional = false;
    @Column(name = "available_today", nullable = false) @Builder.Default private boolean availableToday = true;
    @Column(name = "active", nullable = false) @Builder.Default private boolean active = true;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) @Builder.Default private Instant updatedAt = Instant.now();
    @PreUpdate void touch() { updatedAt = Instant.now(); }
}
