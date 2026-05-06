package org.example.thuvien.service;

import org.example.thuvien.model.Book;
import org.example.thuvien.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    @Autowired // Spring tự tìm Repository và gắn vào đây (Dependency Injection)
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    // Tìm 1 cuốn sách theo ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Xin lỗi, chúng tôi không tìm thấy cuốn sách này! ID: " + id));
    }

    // Tìm 1 cuốn sách theo tên và title
    public List<Book> searchBooks(String title, String author) {
        // 1. Nếu có cả title và author (Điều kiện hẹp nhất -> Ưu tiên số 1)
        if (title != null && !title.isEmpty() && author != null && !author.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author);
        }

        // 2. Nếu chỉ có title
        if (title != null && !title.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        }

        // 3. Nếu chỉ có author
        if (author != null && !author.isEmpty()) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        }

        // 4. Nếu không có tham số nào, trả về tất cả sách
        return bookRepository.findAll();
    }

    // Cập nhật sách
    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id); // Kiểm tra tồn tại

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublishedYear(bookDetails.getPublishedYear());

        return bookRepository.save(book);
    }

    // Xóa sách
    public void deleteBook(Long id) {
        Book book = getBookById(id); // Kiểm tra tồn tại
        bookRepository.delete(book);
    }
}