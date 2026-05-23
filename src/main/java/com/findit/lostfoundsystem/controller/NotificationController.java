package com.findit.lostfoundsystem.controller;

import com.findit.lostfoundsystem.dto.NotificationResponseDTO;
import com.findit.lostfoundsystem.mapper.NotificationMapper;
import com.findit.lostfoundsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    // GET /api/notifications
    // Get all my notifications
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<NotificationResponseDTO> response = notificationService
                .getMyNotifications(email)
                .stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/notifications/unread
    // Get only unread notifications
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnread(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<NotificationResponseDTO> response = notificationService
                .getUnreadNotifications(email)
                .stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/notifications/unread/count
    // Get count of unread notifications
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Integer>> countUnread(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        int count = notificationService.countUnread(email);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // PATCH /api/notifications/{id}/read
    // Mark one notification as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/notifications/read-all
    // Mark all notifications as read
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        notificationService.markAllAsRead(email);
        return ResponseEntity.noContent().build();
    }
}
