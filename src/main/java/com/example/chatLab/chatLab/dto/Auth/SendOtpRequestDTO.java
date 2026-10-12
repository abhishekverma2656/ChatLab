package com.example.chatLab.chatLab.dto.Auth;

import jakarta.validation.constraints.NotBlank;

public record SendOtpRequestDTO(
        @NotBlank(message = "Phone number is required")
        String phoneNumber
) {}