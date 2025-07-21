package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Notification;
import com.cosmicdoc.common.repository.NotificationRepository;
import com.cosmicdoc.common.util.JsonDataLoader;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    private final JsonDataLoader jsonDataLoader;
    private final Map<String, Notification> notificationMap = new ConcurrentHashMap<>();

    public NotificationRepositoryImpl(JsonDataLoader jsonDataLoader) {
        this.jsonDataLoader = jsonDataLoader;
        loadNotifications();
    }

    private void loadNotifications() {
        List<Notification> notifications = jsonDataLoader.loadData("notifications.json", "notifications", Notification.class);
        notifications.stream()
            .filter(notification -> notification != null && notification.getId() != null)
            .forEach(notification -> notificationMap.put(notification.getId(), notification));
    }

    @Override
    public List<Notification> findAll() {
        return new ArrayList<>(notificationMap.values());
    }

    @Override
    public Optional<Notification> findById(String id) {
        return Optional.ofNullable(notificationMap.get(id));
    }

    @Override
    public Notification save(Notification notification) {
        notificationMap.put(notification.getId(), notification);
        return notification;
    }

    @Override
    public void deleteById(String id) {
        notificationMap.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return notificationMap.containsKey(id);
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        return notificationMap.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByType(String type) {
        return notificationMap.values().stream()
                .filter(notification -> notification.getType().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByStatus(String status) {
        return notificationMap.values().stream()
                .filter(notification -> notification.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return notificationMap.values().stream()
                .filter(notification -> {
                    LocalDateTime createdAt = notification.getCreatedAt();
                    return !createdAt.isBefore(start) && !createdAt.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndStatus(String userId, String status) {
        return notificationMap.values().stream()
                .filter(notification -> notification.getUserId().equals(userId) 
                        && notification.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndType(String userId, String type) {
        return notificationMap.values().stream()
                .filter(notification -> notification.getUserId().equals(userId) 
                        && notification.getType().equals(type))
                .collect(Collectors.toList());
    }
}
