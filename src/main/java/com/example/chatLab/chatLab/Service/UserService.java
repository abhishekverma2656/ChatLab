
package com.example.chatLab.chatLab.Service;

import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.exception.BusinessException;
import com.example.chatLab.chatLab.exception.ResourceNotFoundException;
import com.example.chatLab.chatLab.exception.ValidationException;
import com.example.chatLab.chatLab.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create User
    @Transactional
    public User createUser(User user) {

        if (user.getPhoneNumber() == null
                || user.getPhoneNumber().isBlank()) {
            throw new ValidationException(
                    "Phone number is required"
            );
        }

        if (userRepository.existsByPhoneNumber(
                user.getPhoneNumber())) {
            throw new BusinessException(
                    "Phone number is already registered"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.save(user);
    }

    // Get User By ID
    @Transactional(readOnly = true)
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );
    }

    // Get User By Phone Number
    @Transactional(readOnly = true)
    public User getUserByPhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new ValidationException(
                    "Phone number is required"
            );
        }

        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with phone number: "
                                        + phoneNumber
                        )
                );
    }

    // Update User Profile
    @Transactional
    public User updateProfile(
            Long id,
            String name,
            String email,
            String about,
            String profileImage
    ) {

        User user = getUserById(id);

        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Name is required"
            );
        }

        user.setName(name);
        user.setEmail(email);
        user.setAbout(about);
        user.setProfileImage(profileImage);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


}