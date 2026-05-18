package com.findit.lostfoundsystem.mapper;

import com.findit.lostfoundsystem.dto.ItemRequestDTO;
import com.findit.lostfoundsystem.dto.ItemResponseDTO;
import com.findit.lostfoundsystem.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item toEntity(ItemRequestDTO dto){
        return Item.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .location(dto.getLocation())
                .date(dto.getDate())
                .itemImageUrl(dto.getItemImageUrl())
                .type(dto.getType())
                .build();
    }

    public ItemResponseDTO toResponseDTO(Item item){
        return ItemResponseDTO.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .category(item.getCategory())
                .location(item.getLocation())
                .date(item.getDate())
                .itemImageUrl(item.getItemImageUrl())
                .type(item.getType())
                .status(item.getStatus())
                .userId(item.getUser().getId())
                .userName(item.getUser().getName())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
