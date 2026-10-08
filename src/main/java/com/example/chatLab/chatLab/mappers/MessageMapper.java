package com.example.chatLab.chatLab.mappers;

import com.example.chatLab.chatLab.Entity.Message;
import com.example.chatLab.chatLab.dto.Message.MessageRequestDTO;
import com.example.chatLab.chatLab.dto.Message.MessageResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    Message toEntity(MessageRequestDTO messageRequestDTO);
    MessageResponseDTO toResponse(Message message);
}
