
package com.example.chatLab.chatLab.Service;

import com.example.chatLab.chatLab.Entity.Message;
import com.example.chatLab.chatLab.dto.ChatListResponseDTO.ChatListResponseDTO;
import com.example.chatLab.chatLab.repository.MessagesRepository;
import com.example.chatLab.chatLab.Entity.Chat;
import com.example.chatLab.chatLab.Entity.ChatParticipants;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.Entity.Contact;
import com.example.chatLab.chatLab.exception.BusinessException;
import com.example.chatLab.chatLab.exception.ResourceNotFoundException;
import com.example.chatLab.chatLab.exception.ValidationException;
import com.example.chatLab.chatLab.repository.ChatParticipantsRepository;
import com.example.chatLab.chatLab.repository.ChatsRepository;
import com.example.chatLab.chatLab.repository.UserRepository;
import com.example.chatLab.chatLab.repository.ContactsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatsRepository chatsRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final MessagesRepository messagesRepository;
    private final ContactsRepository contactsRepository;

    public ChatService(
            ChatsRepository chatsRepository,
            ChatParticipantsRepository participantsRepository,
            UserRepository userRepository,
            EncryptionService encryptionService,
            MessagesRepository messagesRepository,
            ContactsRepository contactsRepository
    ) {
        this.chatsRepository = chatsRepository;
        this.participantsRepository = participantsRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.messagesRepository = messagesRepository;
        this.contactsRepository = contactsRepository;
    }

    // Create a new direct chat or return the existing one
    @Transactional
    public Chat createOrGetDirectChat(Long userId1, Long userId2) {

        if (userId1 == null || userId2 == null) {
            throw new ValidationException("Both user IDs are required");
        }

        if (userId1.equals(userId2)) {
            throw new BusinessException("You cannot create a chat with yourself");
        }

        User user1 = userRepository.findById(userId1)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId1
                        )
                );

        User user2 = userRepository.findById(userId2)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId2
                        )
                );

        // Same pair must produce the same key
        Long firstId = Math.min(userId1, userId2);
        Long secondId = Math.max(userId1, userId2);

        String directKey = firstId + "_" + secondId;

        // Return existing chat if already created
        var existingChat = chatsRepository.findByDirectKey(directKey);

        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        LocalDateTime now = LocalDateTime.now();

        Chat chat = Chat.builder()
                .type("DIRECT")
                .directKey(directKey)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Chat savedChat = chatsRepository.save(chat);

        ChatParticipants participant1 = ChatParticipants.builder()
                .chat(savedChat)
                .user(user1)
                .joinedAt(now)
                .build();

        ChatParticipants participant2 = ChatParticipants.builder()
                .chat(savedChat)
                .user(user2)
                .joinedAt(now)
                .build();

        participantsRepository.saveAll(
                List.of(participant1, participant2)
        );

        return savedChat;
    }

    // Get chat by ID
    @Transactional(readOnly = true)
    public Chat getChatById(Long chatId) {

        if (chatId == null) {
            throw new ValidationException("Chat ID is required");
        }

        return chatsRepository.findById(chatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Chat not found with ID: " + chatId
                        )
                );
    }

    // Get all chats of a user
    @Transactional(readOnly = true)
    public List<Chat> getUserChats(Long userId) {

        if (userId == null) {
            throw new ValidationException("User ID is required");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with ID: " + userId
            );
        }

        return participantsRepository.findAllByUser_Id(userId)
                .stream()
                .map(ChatParticipants::getChat)
                .toList();
    }

    // Check whether a user belongs to a chat
    @Transactional(readOnly = true)
    public boolean isChatParticipant(Long chatId, Long userId) {

        if (chatId == null || userId == null) {
            throw new ValidationException(
                    "Chat ID and user ID are required"
            );
        }

        if (!chatsRepository.existsById(chatId)) {
            throw new ResourceNotFoundException(
                    "Chat not found with ID: " + chatId
            );
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with ID: " + userId
            );
        }

        return participantsRepository
                .existsByChat_IdAndUser_Id(chatId, userId);
    }

    // Get chat list for a user, including other participant's profile image
    @Transactional(readOnly = true)
    public List<ChatListResponseDTO> getUserChatList(Long userId) {

        if (userId == null) {
            throw new ValidationException("User ID is required");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with ID: " + userId
            );
        }

        return participantsRepository.findAllByUser_Id(userId)
                .stream()
                .map(participant -> {

                    Chat chat = participant.getChat();

                    // Find the other participant
                    ChatParticipants otherParticipant =
                            participantsRepository.findAllByChat_Id(chat.getId())
                                    .stream()
                                    .filter(p -> !p.getUser().getId().equals(userId))
                                    .findFirst()
                                    .orElse(null);

                    Long otherUserId = otherParticipant != null
                            ? otherParticipant.getUser().getId()
                            : null;

                    String otherUserName = "GROUP".equalsIgnoreCase(chat.getType())
                            ? "Group chat"
                            : "Unknown user";

                    String otherUserProfileImage = null;

                    // Get original name, saved contact name and profile image
                    if (otherParticipant != null) {

                        User otherUser = otherParticipant.getUser();

                        String originalName = otherUser.getName();

                        otherUserName = contactsRepository
                                .findByOwner_IdAndContactUser_Id(
                                        userId,
                                        otherUser.getId()
                                )
                                .map(Contact::getSavedName)
                                .filter(savedName ->
                                        savedName != null && !savedName.isBlank()
                                )
                                .orElse(originalName);

                        otherUserProfileImage = otherUser.getProfileImage();
                    }

                    // Fetch latest message
                    var latestMessage =
                            messagesRepository
                                    .findFirstByChat_IdOrderByCreatedAtDesc(
                                            chat.getId()
                                    );

                    String lastMessage = latestMessage
                            .map(Message::getContent)
                            .map(encryptionService::decrypt)
                            .orElse("No messages yet");

                    var lastMessageAt = latestMessage
                            .map(Message::getCreatedAt)
                            .orElse(chat.getLastMessageAt());

                    return new ChatListResponseDTO(
                            chat.getId(),
                            chat.getType(),
                            otherUserId,
                            otherUserName,
                            lastMessage,
                            lastMessageAt,
                            otherUserProfileImage
                    );

                })
                .sorted((a, b) -> {
                    if (a.getLastMessageAt() == null) return 1;
                    if (b.getLastMessageAt() == null) return -1;

                    return b.getLastMessageAt()
                            .compareTo(a.getLastMessageAt());
                })
                .toList();
    }
}
