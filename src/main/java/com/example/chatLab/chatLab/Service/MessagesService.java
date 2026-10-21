    package com.example.chatLab.chatLab.Service;

    import com.example.chatLab.chatLab.Entity.Chat;
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
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;

    @Service
    public class MessagesService {

        private final MessagesRepository messagesRepository;
        private final ChatsRepository chatsRepository;
        private final UserRepository userRepository;
        private final ChatParticipantsRepository participantsRepository;

        public MessagesService(
                MessagesRepository messagesRepository,
                ChatsRepository chatsRepository,
                UserRepository userRepository,
                ChatParticipantsRepository participantsRepository
        ) {
            this.messagesRepository = messagesRepository;
            this.chatsRepository = chatsRepository;
            this.userRepository = userRepository;
            this.participantsRepository = participantsRepository;
        }

        // Send Message
        @Transactional
        public Message sendMessage(
                Long chatId,
                Long senderId,
                String content,
                String messageType
        ) {

            if (chatId == null || senderId == null) {
                throw new ValidationException(
                        "Chat ID and sender ID are required"
                );
            }

            if (content == null || content.isBlank()) {
                throw new ValidationException(
                        "Message content is required"
                );
            }

            if (messageType == null || messageType.isBlank()) {
                throw new ValidationException(
                        "Message type is required"
                );
            }

            Chat chat = chatsRepository.findById(chatId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Chat not found with ID: " + chatId
                            )
                    );

            User sender = userRepository.findById(senderId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with ID: " + senderId
                            )
                    );

            boolean isParticipant =
                    participantsRepository
                            .existsByChat_IdAndUser_Id(chatId, senderId);

            if (!isParticipant) {
                throw new BusinessException(
                        "You are not a participant of this chat"
                );
            }

            LocalDateTime now = LocalDateTime.now();

            Message message = Message.builder()
                    .chat(chat)
                    .sender(sender)
                    .content(content)
                    .messageType(messageType)
                    .createdAt(now)
                    .build();

            Message savedMessage = messagesRepository.save(message);

            chat.setLastMessageAt(now);
            chat.setUpdatedAt(now);

            chatsRepository.save(chat);

            return savedMessage;
        }

        // Get Chat Messages with Pagination
        @Transactional(readOnly = true)
        public Page<Message> getChatMessages(
                Long chatId,
                Long userId,
                int page,
                int size
        ) {

            if (chatId == null || userId == null) {
                throw new ValidationException(
                        "Chat ID and user ID are required"
                );
            }

            if (page < 0) {
                throw new ValidationException(
                        "Page number cannot be negative"
                );
            }

            if (size < 1 || size > 100) {
                throw new ValidationException(
                        "Page size must be between 1 and 100"
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

            boolean isParticipant =
                    participantsRepository
                            .existsByChat_IdAndUser_Id(chatId, userId);

            if (!isParticipant) {
                throw new BusinessException(
                        "You are not allowed to view this chat"
                );
            }

            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(
                            Sort.Order.asc("createdAt"),
                            Sort.Order.asc("id")
                    )
            );

            return messagesRepository.findAllByChat_Id(
                    chatId,
                    pageable
            );
        }

        // Delete Own Message
        @Transactional
        public void deleteMessage(
                Long messageId,
                Long userId
        ) {

            if (messageId == null || userId == null) {
                throw new ValidationException(
                        "Message ID and user ID are required"
                );
            }

            Message message = messagesRepository.findById(messageId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Message not found with ID: " + messageId
                            )
                    );

            if (!message.getSender().getId().equals(userId)) {
                throw new BusinessException(
                        "You can only delete your own message"
                );
            }

            messagesRepository.delete(message);
        }
    }
