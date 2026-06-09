package org.example.thuvien.service;

import org.example.thuvien.dto.BookResponseDTO;
import org.example.thuvien.exception.ResourceNotFoundException;
import org.example.thuvien.mapper.BookMapper;
import org.example.thuvien.model.Book;
import org.example.thuvien.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired private BookMapper bookMapper;

    public List<BookResponseDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return bookMapper.toResponseDTOList(books);
    }

    public Book saveBook(Book book) {
        book.setAvailableQuantity(book.getTotalQuantity());
        return bookRepository.save(book);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xin lỗi, chúng tôi không tìm thấy cuốn sách này! ID: " + id));
    }

    public List<BookResponseDTO> searchBooks(String title, String author, String categoryName) {
        if (isStringEmpty(title) && isStringEmpty(author) && isStringEmpty(categoryName)) {
            List<Book> allBooks = bookRepository.findAll();
            return bookMapper.toResponseDTOList(allBooks);
        }

        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
                title, author, categoryName
        );
        return bookMapper.toResponseDTOList(books);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublishedYear(bookDetails.getPublishedYear());
        book.setTotalQuantity(bookDetails.getTotalQuantity());
        book.setLocation(bookDetails.getLocation());

        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }

    private boolean isStringEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
