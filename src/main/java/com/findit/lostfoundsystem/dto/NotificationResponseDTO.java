package com.findit.lostfoundsystem.dto;

import com.findit.lostfoundsystem.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private Long relatedItemId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
