package com.example.chatLab.chatLab.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.chatLab.chatLab.Entity.Contact;
import com.example.chatLab.chatLab.Service.ContactService;
import com.example.chatLab.chatLab.dto.Contact.ContactRequestDTO;
import com.example.chatLab.chatLab.dto.Contact.ContactResponseDTO;
import com.example.chatLab.chatLab.mappers.ContactMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final ContactMapper contactMapper;

    // Add Contact

    @PostMapping("/{ownerId}/{contactUserId}")
    public ContactResponseDTO addContact(
            @PathVariable Long ownerId,
            @PathVariable Long contactUserId,
            @RequestBody ContactRequestDTO requestDTO) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
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
            @PathVariable Long ownerId) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
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
            @RequestBody ContactRequestDTO requestDTO) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
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
            @PathVariable Long contactUserId) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(ownerId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You can only remove contacts from your own contact list"
            );
        }

        contactService.removeContact(currentUserId, contactUserId);
    }


    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }
}
