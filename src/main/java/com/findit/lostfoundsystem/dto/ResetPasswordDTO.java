package com.findit.lostfoundsystem.dto;

public record ResetPasswordDTO(
        String token,
        String newPassword) {}
