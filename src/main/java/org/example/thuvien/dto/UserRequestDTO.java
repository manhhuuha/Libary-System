package org.example.thuvien.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequestDTO(
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
        String phoneNumber,

        @NotBlank(message = "CCCD không được để trống")
        String identityCard,

        String username,
        String password,
        String userType
) {}
