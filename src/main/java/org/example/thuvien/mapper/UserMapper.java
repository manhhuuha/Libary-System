package org.example.thuvien.mapper;

import org.example.thuvien.dto.UserResponseDTO;
import org.example.thuvien.dto.UserRequestDTO;
import org.example.thuvien.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Chuyển từ Entity sang DTO để trả về cho Client
    UserResponseDTO toResponseDTO(User user);

    // Chuyển danh sách Entity sang danh sách DTO
    List<UserResponseDTO> toResponseDTOList(List<User> users);

    // Chuyển từ RequestDTO (dữ liệu nhập vào) sang Entity để lưu DB
    // Chúng ta bỏ qua id vì id sẽ tự tăng trong DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "role", constant = "PATRON") // Mặc định tạo người dùng là PATRON (Khách)
    User toEntity(UserRequestDTO requestDTO);
}