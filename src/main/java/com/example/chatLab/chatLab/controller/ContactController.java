package com.example.chatLab.chatLab.Controller;


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

        Contact contact = contactService.addContact(
                ownerId,
                contactUserId,
                requestDTO.getSavedName()
        );

        return contactMapper.toResponse(contact);
    }

    // Get My Contacts
    @GetMapping("/{ownerId}")
    public List<ContactResponseDTO> getMyContacts(
            @PathVariable Long ownerId) {

        return contactService.getMyContacts(ownerId)
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

        Contact contact = contactService.updateContactName(
                ownerId,
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

        contactService.removeContact(ownerId, contactUserId);
    }
}
