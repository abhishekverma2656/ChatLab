
package com.example.chatLab.chatLab.Websocket;

import com.example.chatLab.chatLab.Service.ChatService;
import com.example.chatLab.chatLab.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("WEBSOCKET INTERCEPTOR CALLED");

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        // Allow connection cleanup without authentication
        if (StompCommand.DISCONNECT.equals(command)) {
            return message;
        }

        // Authenticate STOMP connection using JWT
        if (StompCommand.CONNECT.equals(command)) {

            String authorization =
                    accessor.getFirstNativeHeader("Authorization");

            if (authorization == null ||
                    !authorization.startsWith("Bearer ")) {

                System.out.println(
                        "SECURITY TEST: JWT MISSING - CONNECTION REJECTED"
                );

                throw new SecurityException(
                        "WebSocket JWT token missing"
                );
            }

            String token = authorization.substring(7);

            if (!jwtService.isTokenValid(token)) {

                System.out.println(
                        "SECURITY TEST: INVALID JWT - CONNECTION REJECTED"
                );

                throw new SecurityException(
                        "Invalid or expired WebSocket JWT"
                );
            }

            Long userId = jwtService.extractUserId(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of()
                    );

            accessor.setUser(authentication);

            System.out.println(
                    "SECURITY TEST: VALID JWT - USER AUTHENTICATED"
            );

            return message;
        }

        // All other STOMP commands require authentication
        Principal principal = accessor.getUser();

        if (principal == null) {
            throw new SecurityException(
                    "WebSocket user is not authenticated"
            );
        }

        // Check chat membership before subscription
        if (StompCommand.SUBSCRIBE.equals(command)) {

            String destination = accessor.getDestination();

            if (destination == null ||
                    !destination.matches("/topic/chat/[0-9]+")) {

                throw new SecurityException(
                        "Subscription destination not allowed"
                );
            }

            Long chatId = Long.valueOf(
                    destination.substring("/topic/chat/".length())
            );

            Long userId = Long.valueOf(principal.getName());

            if (!chatService.isChatParticipant(chatId, userId)) {

                System.out.println(
                        "SECURITY TEST: UNAUTHORIZED CHAT SUBSCRIPTION REJECTED"
                );

                throw new SecurityException(
                        "You are not a participant of this chat"
                );
            }

            System.out.println(
                    "SECURITY TEST: CHAT SUBSCRIPTION ALLOWED"
            );
        }

        // Messages must be sent through REST API
        if (StompCommand.SEND.equals(command)) {
            throw new SecurityException(
                    "Direct STOMP sending is disabled. Use REST API."
            );
        }

        return message;
    }
}
