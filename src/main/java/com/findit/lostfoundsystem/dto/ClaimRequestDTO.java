package com.findit.lostfoundsystem.dto;

public record ClaimRequestDTO (
        Long itemId,
        String message,
        String proofAttachmentUrl
){}
