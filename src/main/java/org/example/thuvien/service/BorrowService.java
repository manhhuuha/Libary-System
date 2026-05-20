package org.example.thuvien.service;

import org.example.thuvien.model.Book;
import org.example.thuvien.model.BookStatus;
import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.model.User;
import org.example.thuvien.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BorrowService {
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private UserService userService;
    @Autowired private BookService bookService;

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        // Kiểm tra trực tiếp trạng thái
        if (book.getStatus() == BookStatus.BORROWED) {
            throw new RuntimeException("Xin lỗi, sách này hiện đã có người mượn rồi!");
        }

        // Kiểm tra luật "Tối đa 3 cuốn"
        if (borrowRepository.countByUserIdAndReturnDateIsNull(userId) >= 3) {
            throw new RuntimeException("Bạn đã mượn đủ giới hạn 3 cuốn sách!");
        }

        // Kiểm tra luật "Không được có sách quá hạn"
        if (borrowRepository.existsByUserIdAndReturnDateIsNullAndDueDateBefore(userId, LocalDate.now())) {
            throw new RuntimeException("Bạn đang có sách quá hạn chưa trả, không thể mượn thêm!");
        }

        // Đổi trạng thái sang Đã mượn
        book.setStatus(BookStatus.BORROWED);
        bookService.saveBook(book); // Lưu lại trạng thái mới cho sách

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        return borrowRepository.save(record);
    }

    public BorrowRecord returnBook(Long bookId) {
        BorrowRecord record = borrowRepository.findByBookIdAndReturnDateIsNull(bookId)
                .orElseThrow(() -> new RuntimeException("Dữ liệu mượn trả không hợp lệ!"));

        record.setReturnDate(LocalDate.now());

        // Trả sách xong thì đổi lại trạng thái Có sẵn
        Book book = record.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookService.saveBook(book);

        return borrowRepository.save(record);
    }

    public long countBookNotReturn(){
        return borrowRepository.countByReturnDateIsNull();
    }
}