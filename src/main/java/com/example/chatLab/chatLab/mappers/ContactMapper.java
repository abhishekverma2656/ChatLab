
package com.example.chatLab.chatLab.mappers;

import com.example.chatLab.chatLab.Entity.Contact;
import com.example.chatLab.chatLab.dto.Contact.ContactRequestDTO;
import com.example.chatLab.chatLab.dto.Contact.ContactResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "contactUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Contact toEntity(ContactRequestDTO contactRequestDTO);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "contactUserId", source = "contactUser.id")
    ContactResponseDTO toResponse(Contact contact);
}
