package com.findit.lostfoundsystem.dto;

public record ChangePasswordDTO(
        String currentPassword,
        String newPassword) {}
