package com.findit.lostfoundsystem.repository;

import com.findit.lostfoundsystem.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a user
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Get only unread notifications
    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    // Count unread notifications
    int countByUserIdAndIsReadFalse(Long userId);

    void deleteByRelatedItemId(Long itemId);
}
