package org.example.thuvien.service;

import org.example.thuvien.exception.BusinessException;
import org.example.thuvien.exception.ResourceNotFoundException;
import org.example.thuvien.model.*;
import org.example.thuvien.repository.BookRepository;
import org.example.thuvien.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserService userService;

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        User user = userService.getUserById(userId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách ID: " + bookId));

        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessException("Xin lỗi, sách này hiện đã hết bản có thể mượn!");
        }

        long currentBorrowed = borrowRepository.countByUserIdAndReturnDateIsNull(userId);
        if (currentBorrowed >= 3) {
            throw new BusinessException("Bạn đã mượn đủ giới hạn 3 cuốn sách!");
        }

        boolean hasOverdue = borrowRepository.existsByUserIdAndReturnDateIsNullAndDueDateBefore(userId, LocalDate.now());
        if (hasOverdue) {
            throw new BusinessException("Bạn đang có sách quá hạn chưa trả, không thể mượn thêm!");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        if (book.getAvailableQuantity() == 0) {
            book.setStatus(BookStatus.BORROWED);
        }
        bookRepository.save(book);

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        record.setStatus(BorrowStatus.BORROWING);
        return borrowRepository.save(record);
    }

    @Transactional
    public BorrowRecord returnBook(Long bookId) {
        BorrowRecord record = borrowRepository.findByBookIdAndReturnDateIsNull(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Dữ liệu mượn trả không hợp lệ!"));

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);

        Book book = record.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        if (book.getAvailableQuantity() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        bookRepository.save(book);

        return borrowRepository.save(record);
    }

    public long countBookNotReturn() {
        return borrowRepository.countByReturnDateIsNull();
    }

    public List<BorrowRecord> getDueSoon() {
        return borrowRepository.findByStatusAndReturnDateIsNullAndDueDateLessThanEqual(
                BorrowStatus.BORROWING, LocalDate.now().plusDays(3));
    }

    public List<BorrowRecord> getOverdue() {
        List<BorrowRecord> overdueRecords = borrowRepository.findByStatusAndDueDateBefore(
                BorrowStatus.BORROWING, LocalDate.now());
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
}
