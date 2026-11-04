
package com.example.chatLab.chatLab.Service;

import com.example.chatLab.chatLab.Entity.Chat;
import com.example.chatLab.chatLab.Entity.ChatParticipants;
import com.example.chatLab.chatLab.Entity.Contact;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.exception.BusinessException;
import com.example.chatLab.chatLab.exception.ResourceNotFoundException;
import com.example.chatLab.chatLab.exception.ValidationException;
import com.example.chatLab.chatLab.repository.ChatParticipantsRepository;
import com.example.chatLab.chatLab.repository.ContactsRepository;
import com.example.chatLab.chatLab.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactService {

    private final ContactsRepository contactsRepository;
    private final UserRepository userRepository;
    private final ChatParticipantsRepository participantsRepository;

    public ContactService(
            ContactsRepository contactsRepository,
            UserRepository userRepository,
            ChatParticipantsRepository participantsRepository
    ) {
        this.contactsRepository = contactsRepository;
        this.userRepository = userRepository;
        this.participantsRepository = participantsRepository;
    }

    // Get all users with whom the logged-in user has chatted
    @Transactional(readOnly = true)
    public List<Contact> getMyContacts(Long ownerId) {

        if (ownerId == null) {
            throw new ValidationException("Owner ID is required");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + ownerId
                        )
                );

        // Get existing private saved names
        Map<Long, Contact> savedContacts = new LinkedHashMap<>();

        contactsRepository.findAllByOwner_Id(ownerId)
                .forEach(contact ->
                        savedContacts.put(
                                contact.getContactUser().getId(),
                                contact
                        )
                );

        // Get all chats of logged-in user
        List<ChatParticipants> myParticipations =
                participantsRepository.findAllByUser_Id(ownerId);

        // Store unique users
        Map<Long, Contact> contactMap = new LinkedHashMap<>();

        for (ChatParticipants myParticipation : myParticipations) {

            Chat chat = myParticipation.getChat();

            List<ChatParticipants> participants =
                    participantsRepository.findAllByChat_Id(chat.getId());

            for (ChatParticipants participant : participants) {

                User otherUser = participant.getUser();

                // Exclude logged-in user
                if (otherUser.getId().equals(ownerId)) {
                    continue;
                }

                Long otherUserId = otherUser.getId();

                // Avoid duplicate contacts
                if (contactMap.containsKey(otherUserId)) {
                    continue;
                }

                Contact savedContact = savedContacts.get(otherUserId);

                if (savedContact != null) {
                    // Use user's private saved name
                    contactMap.put(otherUserId, savedContact);
                } else {
                    // No saved name: show original profile name
                    Contact temporaryContact = Contact.builder()
                            .owner(owner)
                            .contactUser(otherUser)
                            .savedName(otherUser.getName())
                            .build();

                    // This object is NOT saved in database
                    contactMap.put(otherUserId, temporaryContact);
                }
            }
        }

        return contactMap.values()
                .stream()
                .sorted(Comparator.comparing(
                        Contact::getSavedName,
                        String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
    }

    // Update name or create private contact record for the first time
    @Transactional
    public Contact updateContactName(
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
                    "You cannot edit your own contact name"
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
                                "Contact user not found with ID: " + contactUserId
                        )
                );

        // Check whether a record already exists
        Contact contact = contactsRepository
                .findByOwner_IdAndContactUser_Id(
                        ownerId,
                        contactUserId
                )
                .orElseGet(() ->
                        Contact.builder()
                                .owner(owner)
                                .contactUser(contactUser)
                                .createdAt(LocalDateTime.now())
                                .build()
                );

        contact.setSavedName(savedName.trim());

        return contactsRepository.save(contact);
    }

    // Reset private name: original profile name will appear again
    @Transactional
    public void removeContact(
            Long ownerId,
            Long contactUserId
    ) {

        Contact contact = contactsRepository
                .findByOwner_IdAndContactUser_Id(
                        ownerId,
                        contactUserId
                )
                .orElse(null);

        if (contact != null) {
            contactsRepository.delete(contact);
        }

        // User will still appear in contacts because chat exists.
        // Only the private saved name is removed.
    }
}
