package com.findit.lostfoundsystem.mapper;

import com.findit.lostfoundsystem.dto.ClaimResponseDTO;
import com.findit.lostfoundsystem.model.Claim;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {

    public ClaimResponseDTO toResponseDTO(Claim claim) {
        return ClaimResponseDTO.builder()
                .id(claim.getId())
                .status(claim.getStatus())
                .message(claim.getMessage())
                .proofAttachmentUrl(claim.getProofAttachmentUrl())

                .itemId(claim.getItem().getId())
                .itemTitle(claim.getItem().getTitle())
                .itemCategory(claim.getItem().getCategory())
                .itemLocation(claim.getItem().getLocation())

                .claimantId(claim.getClaimant().getId())
                .claimantName(claim.getClaimant().getName())

                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
