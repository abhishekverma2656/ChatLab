package com.example.chatLab.chatLab.mappers;

import com.example.chatLab.chatLab.dto.User.UserResponseDTO;
import com.example.chatLab.chatLab.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "about", target = "about")
    @Mapping(source = "profileImage", target = "profileImage")
    @Mapping(source = "lastSeen", target = "lastSeen")
    UserResponseDTO toResponse(User user);
}