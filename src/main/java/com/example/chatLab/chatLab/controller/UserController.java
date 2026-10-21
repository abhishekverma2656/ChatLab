package com.example.chatLab.chatLab.Controller;



import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.Service.UserService;
import com.example.chatLab.chatLab.dto.User.UserRequestDTO;
import com.example.chatLab.chatLab.dto.User.UserResponseDTO;
import com.example.chatLab.chatLab.mappers.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

//    // Create User
//    @PostMapping
//    public UserResponseDTO createUser(
//            @RequestBody UserRequestDTO requestDTO) {
//
//        User user = userMapper.toEntity(requestDTO);
//        User savedUser = userService.createUser(user);
//
//        return userMapper.toResponse(savedUser);
//    }

    // Get User By ID
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(
            @PathVariable Long id) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You cannot access another user's profile"
            );
        }

        User user = userService.getUserById(currentUserId);

        return userMapper.toResponse(user);
    }

    // Get User By Phone Number
    @GetMapping("/phone/{phoneNumber}")
    public UserResponseDTO getUserByPhoneNumber(
            @PathVariable String phoneNumber) {

        Long currentUserId = getCurrentUserId();

        User user = userService.getUserByPhoneNumber(phoneNumber);

        if (currentUserId.equals(user.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "You cannot search for yourself"
            );
        }

        return userMapper.toResponse(user);
    }

    // Update User Profile
    @PutMapping("/{id}")
    public UserResponseDTO updateProfile(
            @PathVariable Long id,
            @RequestBody UserRequestDTO requestDTO) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You cannot update another user's profile"
            );
        }

        User updatedUser = userService.updateProfile(
                currentUserId,
                requestDTO.getName(),
                requestDTO.getEmail(),
                requestDTO.getAbout(),
                requestDTO.getProfileImage()
        );

        return userMapper.toResponse(updatedUser);
    }

    // Check Phone Number
//    @GetMapping("/exists")
//    public boolean existsByPhoneNumber(
//            @RequestParam String phoneNumber) {
//
//        return userService.existsByPhoneNumber(phoneNumber);
//    }

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }
}

