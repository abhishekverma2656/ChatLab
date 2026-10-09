package com.example.chatLab.chatLab.mappers;


import com.example.chatLab.chatLab.Entity.Contact;
import com.example.chatLab.chatLab.dto.Contact.ContactRequestDTO;
import com.example.chatLab.chatLab.dto.Contact.ContactResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    Contact toEntity (ContactRequestDTO contactRequestDTO);
    ContactResponseDTO toResponse(Contact contact);

}
