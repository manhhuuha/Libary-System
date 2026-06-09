package org.example.thuvien.mapper;

import org.example.thuvien.dto.UserResponseDTO;
import org.example.thuvien.dto.UserRequestDTO;
import org.example.thuvien.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseDTOList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "role", constant = "PATRON")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(UserRequestDTO requestDTO);
}
