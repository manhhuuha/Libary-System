package org.example.thuvien.dto;

import org.example.thuvien.model.BorrowStatus;
import java.time.LocalDate;

public record BorrowHistoryDTO(
        Long recordId,
        String bookTitle,   // Tên cuốn sách đã mượn
        String userName,    // Tên người mượn
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,
        BorrowStatus status
) {}