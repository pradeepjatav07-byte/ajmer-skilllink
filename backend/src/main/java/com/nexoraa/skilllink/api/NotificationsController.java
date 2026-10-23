package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.NotificationEntity;
import com.nexoraa.skilllink.domain.UserEntity;
import com.nexoraa.skilllink.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationsController {
    private final NotificationRepository notifications;
    @GetMapping
    @Transactional(readOnly = true)
    public List<NotificationView> list(@AuthenticationPrincipal UserEntity user) {
        return notifications.findAllByUser_IdOrderByCreatedAtDesc(user.getId()).stream().map(this::view).toList();
    }
    @PatchMapping("/{id}/read")
    @Transactional
    public NotificationView markRead(@PathVariable String id, @AuthenticationPrincipal UserEntity user) {
        NotificationEntity n = notifications.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification was not found."));
        if (!n.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update this notification.");
        n.setReadAt(Instant.now());
        return view(notifications.save(n));
    }
    private NotificationView view(NotificationEntity n) { return new NotificationView(n.getId(), n.getTitle(), n.getBody(), n.getKind(), n.getReadAt()!=null, n.getCreatedAt()); }
    public record NotificationView(String id, String title, String body, String kind, boolean read, Instant createdAt) {}
}
