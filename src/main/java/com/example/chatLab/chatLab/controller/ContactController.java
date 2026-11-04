
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

    // Get all chat participants as contacts
    @GetMapping
    public List<ContactResponseDTO> getMyContacts() {

        Long currentUserId = getCurrentUserId();

        return contactService.getMyContacts(currentUserId)
                .stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    // Update or create private saved name
    @PutMapping("/{contactUserId}")
    public ContactResponseDTO updateContactName(
            @PathVariable Long contactUserId,
            @RequestBody ContactRequestDTO requestDTO
    ) {

        Long currentUserId = getCurrentUserId();

        Contact contact = contactService.updateContactName(
                currentUserId,
                contactUserId,
                requestDTO.getSavedName()
        );

        return contactMapper.toResponse(contact);
    }

    // Reset private saved name
    @DeleteMapping("/{contactUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeContact(
            @PathVariable Long contactUserId
    ) {

        Long currentUserId = getCurrentUserId();

        contactService.removeContact(
                currentUserId,
                contactUserId
        );
    }
}
