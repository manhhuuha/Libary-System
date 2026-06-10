package org.example.thuvien.service;

import org.example.thuvien.exception.BusinessException;
import org.example.thuvien.exception.ResourceNotFoundException;
import org.example.thuvien.model.*;
import org.example.thuvien.repository.BookCopyRepository;
import org.example.thuvien.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private BookCopyRepository bookCopyRepository;
    @Autowired private UserService userService;
    @Autowired private NotificationService notificationService;

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookCopyId, LocalDate dueDate) {
        User user = userService.getUserById(userId);
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản sách ID: " + bookCopyId));

        if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new BusinessException("Bản sách này hiện không có sẵn để mượn!");
        }

        long currentBorrowed = borrowRepository.countByUserIdAndReturnDateIsNull(userId);
        if (currentBorrowed >= 3) {
            throw new BusinessException("Bạn đã mượn đủ giới hạn 3 cuốn sách!");
        }

        boolean hasOverdue = borrowRepository.existsByUserIdAndReturnDateIsNullAndDueDateBefore(userId, LocalDate.now());
        if (hasOverdue) {
            throw new BusinessException("Bạn đang có sách quá hạn chưa trả, không thể mượn thêm!");
        }

        bookCopy.setStatus(BookCopyStatus.BORROWED);
        bookCopyRepository.save(bookCopy);

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBookCopy(bookCopy);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusDays(14));
        record.setStatus(BorrowStatus.BORROWING);
        record.setEmailSent(false);
        return borrowRepository.save(record);
    }

    @Transactional
    public BorrowRecord returnBook(Long bookCopyId) {
        BorrowRecord record = borrowRepository.findByBookCopyIdAndReturnDateIsNull(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Bản sách này chưa được mượn hoặc đã trả!"));

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);

        BookCopy bookCopy = record.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(bookCopy);

        return borrowRepository.save(record);
    }

    public long countBookNotReturn() {
        return borrowRepository.countByReturnDateIsNull();
    }

    public List<BorrowRecord> getAllCurrentBorrows() {
        return borrowRepository.findByReturnDateIsNull();
    }

    public List<BorrowRecord> getDueSoon() {
        return borrowRepository.findByStatusAndReturnDateIsNullAndDueDateLessThanEqual(
                BorrowStatus.BORROWING, LocalDate.now().plusDays(3));
    }

    public List<BorrowRecord> getOverdue() {
        List<BorrowRecord> overdueRecords = borrowRepository.findByStatusesAndDueDateBefore(
                List.of(BorrowStatus.BORROWING, BorrowStatus.OVERDUE), LocalDate.now());
        for (BorrowRecord record : overdueRecords) {
            if (record.getStatus() != BorrowStatus.OVERDUE) {
                record.setStatus(BorrowStatus.OVERDUE);
                borrowRepository.save(record);
            }
        }
        return overdueRecords;
    }

    public List<BorrowRecord> getCurrentBorrowsByUser(Long userId) {
        return borrowRepository.findByUserIdAndReturnDateIsNull(userId);
    }

    public List<BorrowRecord> getBorrowHistoryByUser(Long userId) {
        return borrowRepository.findByUserId(userId);
    }

    public List<BorrowRecord> getAllBorrowHistory() {
        return borrowRepository.findAllByOrderByBorrowDateDesc();
    }

    @Transactional
    public String sendReminder(Long recordId) {
        BorrowRecord record = borrowRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi mượn ID: " + recordId));
        return notificationService.sendReminder(record);
    }
}
