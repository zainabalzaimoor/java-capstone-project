package com.findit.lostfoundsystem.dto;

import com.findit.lostfoundsystem.enums.ItemType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequestDTO {

    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDate date;
    private String itemImageUrl;
    private ItemType type;

}
