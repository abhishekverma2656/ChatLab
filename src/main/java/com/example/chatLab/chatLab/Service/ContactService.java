package com.example.chatLab.chatLab.Service;

import com.example.chatLab.chatLab.Entity.Contact;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.exception.BusinessException;
import com.example.chatLab.chatLab.exception.ResourceNotFoundException;
import com.example.chatLab.chatLab.exception.ValidationException;
import com.example.chatLab.chatLab.repository.ContactsRepository;
import com.example.chatLab.chatLab.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    private final ContactsRepository contactsRepository;
    private final UserRepository userRepository;

    public ContactService(
            ContactsRepository contactsRepository,
            UserRepository userRepository
    ) {
        this.contactsRepository = contactsRepository;
        this.userRepository = userRepository;
    }

    // Add Contact
    @Transactional
    public Contact addContact(
            Long ownerId,
            Long contactUserId,
            String savedName
    ) {

        if (ownerId == null || contactUserId == null) {
            throw new ValidationException(
                    "Owner ID and contact user ID are required"
            );
        }

        if (savedName == null || savedName.isBlank()) {
            throw new ValidationException(
                    "Saved contact name is required"
            );
        }

        if (ownerId.equals(contactUserId)) {
            throw new BusinessException(
                    "You cannot add yourself as a contact"
            );
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with ID: " + ownerId
                        )
                );

        User contactUser = userRepository.findById(contactUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact user not found with ID: "
                                        + contactUserId
                        )
                );

        if (contactsRepository
                .existsByOwner_IdAndContactUser_Id(
                        ownerId, contactUserId)) {
            throw new BusinessException(
                    "Contact is already saved"
            );
        }

        Contact contact = Contact.builder()
                .owner(owner)
                .contactUser(contactUser)
                .savedName(savedName.trim())
                .createdAt(LocalDateTime.now())
                .build();

        return contactsRepository.save(contact);
    }

    // Get My Contacts
    @Transactional(readOnly = true)
    public List<Contact> getMyContacts(Long ownerId) {

        if (ownerId == null) {
            throw new ValidationException(
                    "Owner ID is required"
            );
        }

        if (!userRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException(
                    "User not found with ID: " + ownerId
            );
        }

        return contactsRepository.findAllByOwner_Id(ownerId);
    }

    // Update Saved Contact Name
    @Transactional
    public Contact updateContactName(
            Long ownerId,
            Long contactUserId,
            String savedName
    ) {

        if (savedName == null || savedName.isBlank()) {
            throw new ValidationException(
                    "Saved contact name is required"
            );
        }

        Contact contact = contactsRepository
                .findByOwner_IdAndContactUser_Id(
                        ownerId, contactUserId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact not found in your contact list"
                        )
                );

        contact.setSavedName(savedName.trim());

        return contactsRepository.save(contact);
    }

    // Remove Contact
    @Transactional
    public void removeContact(
            Long ownerId,
            Long contactUserId
    ) {

        Contact contact = contactsRepository
                .findByOwner_IdAndContactUser_Id(
                        ownerId, contactUserId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact not found in your contact list"
                        )
                );

        contactsRepository.delete(contact);
    }
}
