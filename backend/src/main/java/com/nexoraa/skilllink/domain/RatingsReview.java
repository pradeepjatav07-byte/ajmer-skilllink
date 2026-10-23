package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "ratings_reviews", indexes = @Index(name = "idx_review_provider_created", columnList = "provider_id,created_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingsReview {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id", nullable = false, unique = true) private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private UserEntity customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "provider_id", nullable = false) private UserEntity provider;
    @Column(nullable = false) private int rating;
    @Column(length = 1200) private String reviewText;
    @Column(nullable = false) @Builder.Default private boolean reported = false;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
}
