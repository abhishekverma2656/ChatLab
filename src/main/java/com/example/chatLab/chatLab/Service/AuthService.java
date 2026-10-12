
package com.example.chatLab.chatLab.Service;

import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.exception.BusinessException;
import com.example.chatLab.chatLab.exception.ValidationException;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AuthService {

    private static final long OTP_EXPIRY_MS = 5 * 60 * 1000;
    private static final long VERIFICATION_EXPIRY_MS = 5 * 60 * 1000;
    private static final int MAX_ATTEMPTS = 5;

    private final UserService userService;
    private final SecureRandom secureRandom = new SecureRandom();

    private final ConcurrentMap<String, OtpData> otpStore =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Long> verifiedPhones =
            new ConcurrentHashMap<>();

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    // Testing only: generate OTP and return it for development
    public String sendOtp(String phoneNumber) {
        validatePhoneNumber(phoneNumber);

        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        // Invalidate previous OTP and verification state
        otpStore.remove(normalizedPhone);
        verifiedPhones.remove(normalizedPhone);

        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        long expiresAt = System.currentTimeMillis() + OTP_EXPIRY_MS;

        otpStore.put(normalizedPhone, new OtpData(otp, expiresAt));

        // DEV ONLY: never expose OTP in a production API
        return otp;
    }

    public void verifyOtp(String phoneNumber, String otp) {
        validatePhoneNumber(phoneNumber);

        if (otp == null || !otp.matches("\\d{6}")) {
            throw new ValidationException(
                    "OTP must contain exactly 6 digits"
            );
        }

        String normalizedPhone = normalizePhoneNumber(phoneNumber);
        OtpData otpData = otpStore.get(normalizedPhone);

        if (otpData == null) {
            throw new BusinessException(
                    "OTP not found. Please request a new OTP"
            );
        }

        synchronized (otpData) {
            if (System.currentTimeMillis() > otpData.expiresAt) {
                otpStore.remove(normalizedPhone, otpData);
                throw new BusinessException(
                        "OTP has expired. Please request a new OTP"
                );
            }

            if (otpData.attempts >= MAX_ATTEMPTS) {
                otpStore.remove(normalizedPhone, otpData);
                throw new BusinessException(
                        "Maximum OTP attempts exceeded. Request a new OTP"
                );
            }

            if (!otpData.otp.equals(otp)) {
                otpData.attempts++;

                if (otpData.attempts >= MAX_ATTEMPTS) {
                    otpStore.remove(normalizedPhone, otpData);
                    throw new BusinessException(
                            "Maximum OTP attempts exceeded. Request a new OTP"
                    );
                }

                throw new BusinessException(
                        "Invalid OTP. Attempts remaining: "
                                + (MAX_ATTEMPTS - otpData.attempts)
                );
            }

            // OTP is one-time use
            if (!otpStore.remove(normalizedPhone, otpData)) {
                throw new BusinessException(
                        "OTP is no longer valid. Request a new OTP"
                );
            }

            verifiedPhones.put(
                    normalizedPhone,
                    System.currentTimeMillis() + VERIFICATION_EXPIRY_MS
            );
        }
    }

    public User registerUser(String phoneNumber, String name) {
        validatePhoneNumber(phoneNumber);

        if (name == null || name.isBlank()) {
            throw new ValidationException("Name is required");
        }

        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        // Consume verification so it cannot be reused
        consumeVerifiedPhone(normalizedPhone);

        if (userService.existsByPhoneNumber(normalizedPhone)) {
            throw new BusinessException(
                    "User is already registered. Please log in"
            );
        }

        User user = User.builder()
                .name(name.trim())
                .phoneNumber(normalizedPhone)
                .build();

        return userService.createUser(user);
    }

    public User loginUser(String phoneNumber) {
        validatePhoneNumber(phoneNumber);

        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        // Consume verification before login lookup
        consumeVerifiedPhone(normalizedPhone);

        return userService.getUserByPhoneNumber(normalizedPhone);
    }

    private void consumeVerifiedPhone(String phoneNumber) {
        Long expiresAt = verifiedPhones.remove(phoneNumber);

        if (expiresAt == null) {
            throw new BusinessException(
                    "Please verify your OTP first"
            );
        }

        if (System.currentTimeMillis() > expiresAt) {
            throw new BusinessException(
                    "Phone verification has expired. Verify OTP again"
            );
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new ValidationException("Phone number is required");
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber.trim();
    }

    private static class OtpData {
        private final String otp;
        private final long expiresAt;
        private int attempts;

        private OtpData(String otp, long expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
            this.attempts = 0;
        }
    }
    public boolean isRegistered(String phoneNumber) {
        return userService.existsByPhoneNumber(
                phoneNumber.trim()
        );
    }

}
