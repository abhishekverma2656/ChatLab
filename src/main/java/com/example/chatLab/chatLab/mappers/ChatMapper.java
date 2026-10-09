package com.example.chatLab.chatLab.mappers;

import com.example.chatLab.chatLab.Entity.Chat;
import com.example.chatLab.chatLab.dto.Chat.ChatResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {

      Chat toEntity(com.example.chatLab.chatLab.dto.Chat.ChatRequestDTO chatRequestDTO);

      ChatResponseDTO toResponse(Chat chat);

}
