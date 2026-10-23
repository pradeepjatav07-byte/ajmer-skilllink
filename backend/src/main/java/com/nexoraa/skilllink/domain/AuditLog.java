package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = @Index(name = "idx_audit_created", columnList = "created_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "actor_id") private UserEntity actor;
    @Column(name = "action_key", nullable = false, length = 80) private String actionKey;
    @Column(name = "target_type", length = 80) private String targetType;
    @Column(name = "target_id", length = 80) private String targetId;
    @Column(length = 1200) private String details;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
}
