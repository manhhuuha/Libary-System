package org.example.thuvien.service;

import org.example.thuvien.model.Book;
import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.model.User;
import org.example.thuvien.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BorrowService {
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private UserService userService;
    @Autowired private BookService bookService;

    public BorrowRecord borrowBook(Long userId, Long bookId) {
        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        // Dùng hàm tồn tại để kiểm tra cực nhanh
        if (borrowRepository.existsByBookIdAndReturnDateIsNull(bookId)) {
            throw new RuntimeException("Sách này hiện đang được mượn, chưa trả!");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        return borrowRepository.save(record);
    }

    public BorrowRecord returnBook(Long bookId) {
        // Tìm bản ghi mượn của cuốn sách này mà chưa trả
        BorrowRecord record = borrowRepository.findByBookIdAndReturnDateIsNull(bookId)
                .orElseThrow(() -> new RuntimeException("Cuốn sách này hiện không có ai mượn hoặc đã trả rồi!"));

        record.setReturnDate(LocalDate.now());
        return borrowRepository.save(record);
    }

    public long countBookNotReturn(){
        return borrowRepository.countByReturnDateIsNull();
    }
}