package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    List<AuditLog> findTop100ByOrderByCreatedAtDesc();
}
