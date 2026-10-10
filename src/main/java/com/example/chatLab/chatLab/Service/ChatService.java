package com.example.chatLab.chatLab.Service;

import com.example.chatLab.chatLab.Entity.Chat;
import com.example.chatLab.chatLab.Entity.ChatParticipants;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.exception.BusinessException;
import com.example.chatLab.chatLab.exception.ResourceNotFoundException;
import com.example.chatLab.chatLab.exception.ValidationException;
import com.example.chatLab.chatLab.repository.ChatParticipantsRepository;
import com.example.chatLab.chatLab.repository.ChatsRepository;
import com.example.chatLab.chatLab.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatsRepository chatsRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final UserRepository userRepository;

    public ChatService(
            ChatsRepository chatsRepository,
            ChatParticipantsRepository participantsRepository,
            UserRepository userRepository
    ) {
        this.chatsRepository = chatsRepository;
        this.participantsRepository = participantsRepository;
        this.userRepository = userRepository;
    }

    // Create a new direct chat or return the existing one
    @Transactional
    public Chat createOrGetDirectChat(Long userId1, Long userId2) {

        if (userId1 == null || userId2 == null) {
            throw new ValidationException(
                    "Both user IDs are required"
            );
        }

        if (userId1.equals(userId2)) {
            throw new BusinessException(
                    "You cannot create a chat with yourself"
            );
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
            throw new ValidationException(
                    "Chat ID is required"
            );
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
            throw new ValidationException(
                    "User ID is required"
            );
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
}
