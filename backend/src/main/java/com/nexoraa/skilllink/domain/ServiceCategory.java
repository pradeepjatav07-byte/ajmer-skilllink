package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "service_categories", indexes = @Index(name = "idx_categories_slug", columnList = "slug", unique = true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceCategory {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @Column(nullable = false, length = 80) private String name;
    @Column(nullable = false, unique = true, length = 90) private String slug;
    @Column(length = 300) private String description;
    @Column(nullable = false) @Builder.Default private boolean active = true;
    @Column(nullable = false) @Builder.Default private Instant createdAt = Instant.now();
}
