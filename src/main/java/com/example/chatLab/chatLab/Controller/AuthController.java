
package com.example.chatLab.chatLab.Controller;

import com.example.chatLab.chatLab.Service.JwtService;
import com.example.chatLab.chatLab.dto.Auth.AuthResponseDTO;
import com.example.chatLab.chatLab.dto.Auth.LoginRequestDTO;
import com.example.chatLab.chatLab.dto.Auth.RegisterRequestDTO;
import com.example.chatLab.chatLab.dto.Auth.SendOtpRequestDTO;
import com.example.chatLab.chatLab.dto.Auth.VerifyOtpRequestDTO;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.Service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Value("${app.auth.expose-test-otp:false}")
    private boolean exposeTestOtp;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // Request OTP
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(
            @Valid @RequestBody SendOtpRequestDTO request
    ) {
        String otp = authService.sendOtp(request.phoneNumber());

        if (exposeTestOtp) {
            return ResponseEntity.ok(Map.of(
                    "message", "Test OTP generated successfully",
                    "testOtp", otp
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "OTP requested successfully"
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequestDTO request) {

        authService.verifyOtp(
                request.phoneNumber(),
                request.otp()
        );

        boolean registered = authService.isRegistered(
                request.phoneNumber()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP verified successfully");
        response.put("registered", registered);

        return ResponseEntity.ok(response);
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        User user = authService.registerUser(
                request.phoneNumber(),
                request.name()
        );
        String token = jwtService.generateToken(
                user.getId(),
                user.getPhoneNumber()
        );

        AuthResponseDTO response = new AuthResponseDTO(
                "Registration successful",
                user.getId(),
                user.getName(),
                user.getPhoneNumber(),
                token
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Login existing user
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        User user = authService.loginUser(request.phoneNumber());

        String token = jwtService.generateToken(
                user.getId(),
                user.getPhoneNumber()
        );

        AuthResponseDTO response = new AuthResponseDTO(
                "Login successful",
                user.getId(),
                user.getName(),
                user.getPhoneNumber(),
                token
        );

        return ResponseEntity.ok(response);
    }
}
