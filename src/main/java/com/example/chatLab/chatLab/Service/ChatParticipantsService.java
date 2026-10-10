package com.example.chatLab.chatLab.Service;

import com.example.chatLab.chatLab.Entity.Chat;
import com.example.chatLab.chatLab.Entity.ChatParticipants;
import com.example.chatLab.chatLab.Entity.Message;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.exception.BusinessException;
import com.example.chatLab.chatLab.exception.ResourceNotFoundException;
import com.example.chatLab.chatLab.exception.ValidationException;
import com.example.chatLab.chatLab.repository.ChatParticipantsRepository;
import com.example.chatLab.chatLab.repository.ChatsRepository;
import com.example.chatLab.chatLab.repository.MessagesRepository;
import com.example.chatLab.chatLab.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatParticipantsService {

    private final ChatParticipantsRepository participantsRepository;
    private final ChatsRepository chatsRepository;
    private final UserRepository userRepository;
    private final MessagesRepository messagesRepository;

    public ChatParticipantsService(
            ChatParticipantsRepository participantsRepository,
            ChatsRepository chatsRepository,
            UserRepository userRepository,
            MessagesRepository messagesRepository
    ) {
        this.participantsRepository = participantsRepository;
        this.chatsRepository = chatsRepository;
        this.userRepository = userRepository;
        this.messagesRepository = messagesRepository;
    }

    // Get all participants of a chat
    @Transactional(readOnly = true)
    public List<ChatParticipants> getParticipantsByChatId(
            Long chatId
    ) {

        if (chatId == null) {
            throw new ValidationException(
                    "Chat ID is required"
            );
        }

        if (!chatsRepository.existsById(chatId)) {
            throw new ResourceNotFoundException(
                    "Chat not found with ID: " + chatId
            );
        }

        return participantsRepository.findAllByChat_Id(chatId);
    }

    // Add a participant to a group chat
    @Transactional
    public ChatParticipants addParticipant(
            Long chatId,
            Long userId
    ) {

        if (chatId == null || userId == null) {
            throw new ValidationException(
                    "Chat ID and user ID are required"
            );
        }

        Chat chat = chatsRepository.findById(chatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Chat not found with ID: " + chatId
                        )
                );

        if (chat.getType() == null
                || !chat.getType().equalsIgnoreCase("GROUP")) {
            throw new BusinessException(
                    "Participants can only be added to group chats"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        if (participantsRepository
                .existsByChat_IdAndUser_Id(chatId, userId)) {
            throw new BusinessException(
                    "User is already a participant of this chat"
            );
        }

        ChatParticipants participant = ChatParticipants.builder()
                .chat(chat)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build();

        return participantsRepository.save(participant);
    }

    // Remove a participant from a group chat
    @Transactional
    public void removeParticipant(
            Long chatId,
            Long userId
    ) {

        if (chatId == null || userId == null) {
            throw new ValidationException(
                    "Chat ID and user ID are required"
            );
        }

        Chat chat = chatsRepository.findById(chatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Chat not found with ID: " + chatId
                        )
                );

        if (chat.getType() == null
                || !chat.getType().equalsIgnoreCase("GROUP")) {
            throw new BusinessException(
                    "Participants can only be removed from group chats"
            );
        }

        ChatParticipants participant =
                participantsRepository
                        .findByChat_IdAndUser_Id(chatId, userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participant not found in this chat"
                                )
                        );

        participantsRepository.delete(participant);
    }

    // Update the last-read message for a participant
    @Transactional
    public ChatParticipants updateLastReadMessage(
            Long chatId,
            Long userId,
            Long messageId
    ) {

        if (chatId == null || userId == null || messageId == null) {
            throw new ValidationException(
                    "Chat ID, user ID and message ID are required"
            );
        }

        ChatParticipants participant =
                participantsRepository
                        .findByChat_IdAndUser_Id(chatId, userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participant not found in this chat"
                                )
                        );

        Message message = messagesRepository.findById(messageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Message not found with ID: " + messageId
                        )
                );

        if (!message.getChat().getId().equals(chatId)) {
            throw new BusinessException(
                    "Message does not belong to this chat"
            );
        }

        participant.setLastReadMessageId(messageId);

        return participantsRepository.save(participant);
    }
}
