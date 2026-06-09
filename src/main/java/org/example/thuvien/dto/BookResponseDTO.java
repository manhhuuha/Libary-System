package org.example.thuvien.dto;

import org.example.thuvien.model.BookStatus;

public record BookResponseDTO(
        Long id,
        String title,
        String author,
        String isbn,
        String location,
        BookStatus status,
        String categoryName,
        int totalQuantity,
        int availableQuantity
) {}
