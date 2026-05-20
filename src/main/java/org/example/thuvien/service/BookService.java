package org.example.thuvien.service;

import org.example.thuvien.dto.BookResponseDTO;
import org.example.thuvien.mapper.BookMapper;
import org.example.thuvien.model.Book;
import org.example.thuvien.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired // Spring tự tìm Repository và gắn vào đây (Dependency Injection)
    private BookRepository bookRepository;

    @Autowired private BookMapper bookMapper; // Tiêm Mapper vào

    public List<BookResponseDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return bookMapper.toResponseDTOList(books);
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
    public List<BookResponseDTO> searchBooks(String title, String author, String categoryName) {
        if (isStringEmpty(title) && isStringEmpty(author) && isStringEmpty(categoryName)) {
            // Nếu không nhập gì, trả về toàn bộ sách trong kho
            List<Book> allBooks = bookRepository.findAll();
            return bookMapper.toResponseDTOList(allBooks);
        }

        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
                title, author, categoryName
        );
        return bookMapper.toResponseDTOList(books);
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

    private boolean isStringEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}