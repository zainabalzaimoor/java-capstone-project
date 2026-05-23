package com.findit.lostfoundsystem.service;

import com.findit.lostfoundsystem.enums.NotificationType;
import com.findit.lostfoundsystem.model.Notification;
import com.findit.lostfoundsystem.model.User;
import com.findit.lostfoundsystem.repository.NotificationRepository;
import com.findit.lostfoundsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // ─── SEND A NOTIFICATION ──────────────────────────────
    public void sendNotification(User user, String message,
                                 NotificationType type, Long relatedItemId) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .relatedItemId(relatedItemId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    // ─── GET MY NOTIFICATIONS ─────────────────────────────
    public List<Notification> getMyNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // ─── GET UNREAD ONLY ──────────────────────────────────
    public List<Notification> getUnreadNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository
                .findByUserIdAndIsReadFalse(user.getId());
    }

    // ─── COUNT UNREAD ─────────────────────────────────────
    public int countUnread(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository
                .countByUserIdAndIsReadFalse(user.getId());
    }

    // ─── MARK ONE AS READ ─────────────────────────────────
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // ─── MARK ALL AS READ ─────────────────────────────────
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> unread = notificationRepository
                .findByUserIdAndIsReadFalse(user.getId());
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }


}
