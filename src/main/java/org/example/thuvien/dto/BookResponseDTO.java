package org.example.thuvien.dto;

import org.example.thuvien.model.BookStatus;

public record BookResponseDTO(
        Long id,
        String title,
        String author,
        String isbn,
        String location,    // Vị trí kệ sách
        BookStatus status,  // Trạng thái (AVAILABLE/BORROWED)
        String categoryName // Chỉ trả về tên lĩnh vực để hiển thị
) {}