package com.sena.barberspa.model.dto;

public record AuthResponse(
        String jwt,
        Long userId,
        String name,
        String phone) {
}