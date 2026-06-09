package org.example.thuvien.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String identityCard,
        String username,
        String role,
        String userType,
        Boolean active,
        LocalDateTime createdAt
) {}
