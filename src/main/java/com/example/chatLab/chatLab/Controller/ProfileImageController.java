package com.example.chatLab.chatLab.Controller;

import com.example.chatLab.chatLab.dto.User.UserResponseDTO;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.mappers.UserMapper;
import com.example.chatLab.chatLab.Service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class ProfileImageController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final Path uploadPath;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public ProfileImageController(
            UserService userService,
            UserMapper userMapper,
            @Value("${app.upload.dir:uploads/profile-images}")
            String uploadDirectory) {

        this.userService = userService;
        this.userMapper = userMapper;

        this.uploadPath = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();
    }

    // Get authenticated user ID
    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }

        return Long.valueOf(authentication.getName());
    }

    // Upload profile image
    @PostMapping(
            value = "/{userId}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserResponseDTO uploadProfileImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot update another user's profile"
            );
        }

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a profile image"
            );
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Image size must not exceed 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(
                        contentType.toLowerCase(Locale.ROOT))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPG, PNG and WEBP images are allowed"
            );
        }

        // Validate user
        userService.getUserById(userId);

        String extension = switch (
                contentType.toLowerCase(Locale.ROOT)) {

            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported image type"
            );
        };

        String filename = UUID.randomUUID() + extension;

        Path destination = uploadPath.resolve(filename).normalize();

        if (!destination.startsWith(uploadPath)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file path"
            );
        }

        try {

            Files.createDirectories(uploadPath);

            Files.copy(file.getInputStream(), destination);

            // Image URL
            String savedImagePath =
                    "/api/users/profile-images/" + filename;

            // Save path in database
            User updatedUser = userService.updateProfileImage(
                    userId,
                    savedImagePath
            );

            // Return DTO containing profileImage
            return userMapper.toResponse(updatedUser);

        } catch (IOException exception) {

            try {
                Files.deleteIfExists(destination);
            } catch (IOException ignored) {
                // Ignore cleanup failure
            }

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to upload profile image",
                    exception
            );
        }
    }

    // Retrieve profile image
    @GetMapping("/profile-images/{filename:.+}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String filename) {

        if (!filename.matches(
                "^[a-fA-F0-9-]{36}\\.(jpg|png|webp)$")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid image filename"
            );
        }

        Path imagePath = uploadPath.resolve(filename).normalize();

        if (!imagePath.startsWith(uploadPath)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid image path"
            );
        }

        try {

            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile image not found"
                );
            }

            String contentType = Files.probeContentType(imagePath);

            MediaType mediaType = contentType == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(contentType);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);

        } catch (IOException exception) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to read profile image",
                    exception
            );
        }
    }
}