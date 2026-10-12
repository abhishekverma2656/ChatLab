package com.example.chatLab.chatLab.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequestDTO(
        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "\\d{6}", message = "OTP must contain 6 digits")
        String otp
) {}