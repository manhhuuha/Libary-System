package org.example.thuvien.dto;

public record UserResponseDTO(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String identityCard,
        String username,
        String role
) {}