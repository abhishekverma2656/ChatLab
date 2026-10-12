package com.example.chatLab.chatLab.dto.Auth;

public record AuthResponseDTO(
        String message,
        Long userId,
        String name,
        String phoneNumber
) {}