package com.example.chatLab.chatLab.mappers;
import com.example.chatLab.chatLab.Entity.User;
import com.example.chatLab.chatLab.dto.User.UserRequestDTO;
import com.example.chatLab.chatLab.dto.User.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDTO userRequestDTO);
    UserResponseDTO toResponse(User user);
}
