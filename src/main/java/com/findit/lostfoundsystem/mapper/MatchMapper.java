package com.findit.lostfoundsystem.mapper;

import com.findit.lostfoundsystem.dto.MatchResponseDTO;
import com.findit.lostfoundsystem.model.Match;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    public MatchResponseDTO toResponseDTO(Match match) {
        return MatchResponseDTO.builder()
                .id(match.getId())
                .matchScore(match.getMatchScore())
                .status(match.getStatus())

                // Lost item
                .lostItemId(match.getLostItem().getId())
                .lostItemTitle(match.getLostItem().getTitle())
                .lostItemCategory(match.getLostItem().getCategory())
                .lostItemLocation(match.getLostItem().getLocation())
                .lostItemUserId(match.getLostItem().getUser().getId())
                .lostItemUserName(match.getLostItem().getUser().getName())

                // Found item
                .foundItemId(match.getFoundItem().getId())
                .foundItemTitle(match.getFoundItem().getTitle())
                .foundItemCategory(match.getFoundItem().getCategory())
                .foundItemLocation(match.getFoundItem().getLocation())
                .foundItemUserId(match.getFoundItem().getUser().getId())
                .foundItemUserName(match.getFoundItem().getUser().getName())

                .createdAt(match.getCreatedAt())
                .updatedAt(match.getUpdatedAt())
                .build();
    }
}
