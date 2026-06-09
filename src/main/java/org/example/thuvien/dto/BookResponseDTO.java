package org.example.thuvien.dto;

public record BookResponseDTO(
        Long id,
        String title,
        String author,
        String location,
        String categoryName
) {}
