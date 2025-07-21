package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Notification;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends BaseRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByType(String type);
    List<Notification> findByStatus(String status);
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Notification> findByUserIdAndStatus(String userId, String status);
    List<Notification> findByUserIdAndType(String userId, String type);
}
