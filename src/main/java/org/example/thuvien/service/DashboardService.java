package org.example.thuvien.service;

import org.example.thuvien.dto.DashboardSummaryDTO;
import org.example.thuvien.model.BorrowStatus;
import org.example.thuvien.repository.BookCopyRepository;
import org.example.thuvien.repository.BookRepository;
import org.example.thuvien.repository.BorrowRepository;
import org.example.thuvien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardSummaryDTO getSummary() {
        long totalBooks = bookRepository.count();
        long totalBookCopies = bookCopyRepository.count();
        long borrowedBooks = borrowRepository.countByStatus(BorrowStatus.BORROWING);
        long overdueBooks = borrowRepository.countByStatus(BorrowStatus.OVERDUE);
        long totalUsers = userRepository.count();

        return new DashboardSummaryDTO(totalBooks, totalBookCopies, borrowedBooks, overdueBooks, totalUsers);
    }
}
