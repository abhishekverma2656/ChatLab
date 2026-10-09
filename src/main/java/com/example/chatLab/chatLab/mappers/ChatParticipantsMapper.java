package com.example.chatLab.chatLab.mappers;

import com.example.chatLab.chatLab.Entity.ChatParticipants;
import com.example.chatLab.chatLab.dto.ChatParticipants.ChatParticipantsRequestDTO;
import com.example.chatLab.chatLab.dto.ChatParticipants.ChatParticipantsResponseDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ChatParticipantsMapper {

    ChatParticipants toEntity(ChatParticipantsRequestDTO chatParticipantsRequestDTO);

    ChatParticipantsResponseDTO toResponse(ChatParticipants chatParticipants);
}
