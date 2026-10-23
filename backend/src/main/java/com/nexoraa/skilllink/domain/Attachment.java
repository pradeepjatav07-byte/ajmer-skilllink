package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "attachments", indexes = @Index(name = "idx_attachment_booking_phase", columnList = "booking_id,phase"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attachment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id", nullable = false) private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "uploaded_by", nullable = false) private UserEntity uploadedBy;
    @Column(nullable = false, length = 12) private String phase;
    @Column(name = "file_name", nullable = false, length = 500) private String fileName;
    @Column(name = "demo_url", nullable = false, length = 1000) private String demoUrl;
    @Column(name = "captured_at", nullable = false) private Instant capturedAt;
    @Column(name = "consent_confirmed", nullable = false) private boolean consentConfirmed;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
}
