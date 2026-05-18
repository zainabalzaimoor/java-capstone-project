package com.findit.lostfoundsystem.dto;

import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDate date;
    private String itemImageUrl;
    private ItemType type;
    private ItemStatus status;

    private Long userId;
    private String userName;

    private LocalDateTime  createdAt;
    private LocalDateTime updatedAt;
}
