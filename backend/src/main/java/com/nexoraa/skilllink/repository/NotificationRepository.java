package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    List<NotificationEntity> findAllByUser_IdOrderByCreatedAtDesc(String userId);
}
