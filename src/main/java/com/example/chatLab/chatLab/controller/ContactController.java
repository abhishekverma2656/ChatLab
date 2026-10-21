
package com.example.chatLab.chatLab.Controller;

import com.example.chatLab.chatLab.Entity.Contact;
import com.example.chatLab.chatLab.Service.ContactService;
import com.example.chatLab.chatLab.dto.Contact.ContactRequestDTO;
import com.example.chatLab.chatLab.dto.Contact.ContactResponseDTO;
import com.example.chatLab.chatLab.mappers.ContactMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final ContactMapper contactMapper;

    // Get authenticated user's ID from Spring Security
    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid authenticated user ID"
            );
        }
    }

    // Add Contact
    @PostMapping("/{ownerId}/{contactUserId}")
    public ContactResponseDTO addContact(
            @PathVariable Long ownerId,
            @PathVariable Long contactUserId,
            @RequestBody ContactRequestDTO requestDTO
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only add contacts to your own contact list"
            );
        }

        Contact contact = contactService.addContact(
                currentUserId,
                contactUserId,
                requestDTO.getSavedName()
        );

        return contactMapper.toResponse(contact);
    }

    // Get My Contacts
    @GetMapping("/{ownerId}")
    public List<ContactResponseDTO> getMyContacts(
            @PathVariable Long ownerId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only view your own contacts"
            );
        }

        return contactService.getMyContacts(currentUserId)
                .stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    // Update Saved Contact Name
    @PutMapping("/{ownerId}/{contactUserId}")
    public ContactResponseDTO updateContactName(
            @PathVariable Long ownerId,
            @PathVariable Long contactUserId,
            @RequestBody ContactRequestDTO requestDTO
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only update your own contacts"
            );
        }

        Contact contact = contactService.updateContactName(
                currentUserId,
                contactUserId,
                requestDTO.getSavedName()
        );

        return contactMapper.toResponse(contact);
    }

    // Remove Contact
    @DeleteMapping("/{ownerId}/{contactUserId}")
    public void removeContact(
            @PathVariable Long ownerId,
            @PathVariable Long contactUserId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only remove contacts from your own contact list"
            );
        }

        contactService.removeContact(currentUserId, contactUserId);
    }
}
