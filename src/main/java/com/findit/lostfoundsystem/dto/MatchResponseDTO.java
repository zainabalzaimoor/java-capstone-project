package com.findit.lostfoundsystem.dto;

import com.findit.lostfoundsystem.enums.MatchStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MatchResponseDTO {

    private Long id;
    private double matchScore;
    private MatchStatus status;

    // Lost item info
    private Long lostItemId;
    private String lostItemTitle;
    private String lostItemCategory;
    private String lostItemLocation;
    private Long lostItemUserId;
    private String lostItemUserName;

    // Found item info
    private Long foundItemId;
    private String foundItemTitle;
    private String foundItemCategory;
    private String foundItemLocation;
    private Long foundItemUserId;
    private String foundItemUserName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}