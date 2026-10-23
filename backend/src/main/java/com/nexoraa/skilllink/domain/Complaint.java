package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "complaints", indexes = @Index(name = "idx_complaints_status_created", columnList = "status,created_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Complaint {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id") private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private UserEntity customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "provider_id") private UserEntity provider;
    @Column(nullable = false, length = 80) private String category;
    @Column(nullable = false, length = 1200) private String description;
    @Column(nullable = false, length = 20) @Builder.Default private String status = "OPEN";
    @Column(nullable = false, length = 20) @Builder.Default private String priority = "NORMAL";
    @Column(name = "resolution_note", length = 1200) private String resolutionNote;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) @Builder.Default private Instant updatedAt = Instant.now();
    @PreUpdate void touch() { updatedAt = Instant.now(); }
}
