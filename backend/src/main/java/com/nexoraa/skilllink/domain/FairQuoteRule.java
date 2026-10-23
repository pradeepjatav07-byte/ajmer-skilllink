package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "fair_quote_rules", indexes = @Index(name = "idx_quote_category_area", columnList = "category_id,area_name"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FairQuoteRule {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id", nullable = false) private ServiceCategory category;
    @Column(name = "area_name", nullable = false, length = 80) @Builder.Default private String areaName = "Ajmer - all areas";
    @Column(name = "estimated_min", nullable = false) private int estimatedMin;
    @Column(name = "estimated_max", nullable = false) private int estimatedMax;
    @Column(name = "visit_fee_min", nullable = false) @Builder.Default private int visitFeeMin = 0;
    @Column(name = "visit_fee_max", nullable = false) @Builder.Default private int visitFeeMax = 0;
    @Column(name = "emergency_surcharge_min", nullable = false) @Builder.Default private int emergencySurchargeMin = 0;
    @Column(name = "emergency_surcharge_max", nullable = false) @Builder.Default private int emergencySurchargeMax = 0;
    @Column(nullable = false) @Builder.Default private boolean active = true;
    @Column(name = "updated_at", nullable = false) @Builder.Default private Instant updatedAt = Instant.now();
}
