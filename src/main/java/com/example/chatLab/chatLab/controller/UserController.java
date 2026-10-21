package com.example.chatLab.chatLab.Controller;



import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.Service.UserService;
import com.example.chatLab.chatLab.dto.User.UserRequestDTO;
import com.example.chatLab.chatLab.dto.User.UserResponseDTO;
import com.example.chatLab.chatLab.mappers.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    // Create User
    @PostMapping
    public UserResponseDTO createUser(
            @RequestBody UserRequestDTO requestDTO) {

        User user = userMapper.toEntity(requestDTO);
        User savedUser = userService.createUser(user);

        return userMapper.toResponse(savedUser);
    }

    // Get User By ID
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(
            @PathVariable Long id) {

        User user = userService.getUserById(id);

        return userMapper.toResponse(user);
    }

    // Get User By Phone Number
    @GetMapping("/phone/{phoneNumber}")
    public UserResponseDTO getUserByPhoneNumber(
            @PathVariable String phoneNumber) {

        User user =
                userService.getUserByPhoneNumber(phoneNumber);

        return userMapper.toResponse(user);
    }

    // Update User Profile
    @PutMapping("/{id}")
    public UserResponseDTO updateProfile(
            @PathVariable Long id,
            @RequestBody UserRequestDTO requestDTO) {

        User updatedUser = userService.updateProfile(
                id,
                requestDTO.getName(),
                requestDTO.getEmail(),
                requestDTO.getAbout(),
                requestDTO.getProfileImage()
        );

        return userMapper.toResponse(updatedUser);
    }

    // Check Phone Number
    @GetMapping("/exists")
    public boolean existsByPhoneNumber(
            @RequestParam String phoneNumber) {

        return userService.existsByPhoneNumber(phoneNumber);
    }
}

