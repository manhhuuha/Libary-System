package org.example.thuvien.dto;

public record DashboardSummaryDTO(
        long totalBooks,
        long totalBookCopies,
        long borrowedBooks,
        long overdueBooks,
        long totalUsers
) {}
