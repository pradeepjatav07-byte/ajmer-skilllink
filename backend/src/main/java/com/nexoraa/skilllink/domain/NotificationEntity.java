package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = @Index(name = "idx_notifications_user_created", columnList = "user_id,created_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private UserEntity user;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, length = 600) private String body;
    @Column(nullable = false, length = 30) private String kind;
    @Column(name = "read_at") private Instant readAt;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
}
