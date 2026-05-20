package com.findit.lostfoundsystem.dto;

import com.findit.lostfoundsystem.enums.ClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClaimResponseDTO {

    private Long id;
    private ClaimStatus status;
    private String message;
    private String proofAttachmentUrl;

    // Item info
    private Long itemId;
    private String itemTitle;
    private String itemCategory;
    private String itemLocation;

    // Claimant info
    private Long claimantId;
    private String claimantName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
