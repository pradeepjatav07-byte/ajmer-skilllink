package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "booking_status_history", indexes = @Index(name = "idx_booking_history_booking_time", columnList = "booking_id,created_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id", nullable = false) private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "changed_by", nullable = false) private UserEntity changedBy;
    @Enumerated(EnumType.STRING) @Column(name = "status_value", nullable = false, length = 32) private BookingStatus status;
    @Column(length = 500) private String note;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
}
