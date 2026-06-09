package org.example.thuvien.service;

import org.example.thuvien.dto.BookResponseDTO;
import org.example.thuvien.exception.ResourceNotFoundException;
import org.example.thuvien.mapper.BookMapper;
import org.example.thuvien.model.Book;
import org.example.thuvien.model.BookCopy;
import org.example.thuvien.model.BookCopyStatus;
import org.example.thuvien.repository.BookCopyRepository;
import org.example.thuvien.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookMapper bookMapper;

    public Page<BookResponseDTO> getBooksPaged(int page, int size, String title, String author, String categoryName) {
        return bookRepository.searchBooksPaged(
                normalize(title), normalize(author), normalize(categoryName),
                PageRequest.of(page, size)
        ).map(bookMapper::toResponseDTO);
    }

    @Transactional
    public Book saveBook(Book book) {
        Book saved = bookRepository.save(book);
        List<String> isbns = book.getCopyIsbns();
        int n = Math.max(book.getNumberOfCopies(), 1);
        for (int i = 0; i < n; i++) {
            String isbn = (isbns != null && i < isbns.size()) ? isbns.get(i) : null;
            createCopy(saved, i + 1, isbn);
        }
        return saved;
    }

    @Transactional
    public List<BookCopy> addCopies(Long bookId, List<String> isbns) {
        Book book = getBookById(bookId);
        int next = bookCopyRepository.findByBookId(bookId).size() + 1;
        List<BookCopy> copies = new java.util.ArrayList<>();
        for (int i = 0; i < isbns.size(); i++) {
            copies.add(createCopy(book, next++, isbns.get(i)));
        }
        return copies;
    }

    private BookCopy createCopy(Book book, int copyNumber, String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN của bản sao " + copyNumber + " không được để trống");
        }
        BookCopy copy = new BookCopy();
        copy.setBook(book);
        copy.setCopyNumber(String.valueOf(copyNumber));
        copy.setIsbn(isbn.trim());
        copy.setStatus(BookCopyStatus.AVAILABLE);
        return bookCopyRepository.save(copy);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xin lỗi, chúng tôi không tìm thấy cuốn sách này! ID: " + id));
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublishedYear(bookDetails.getPublishedYear());
        book.setLocation(bookDetails.getLocation());
        book.setCategory(bookDetails.getCategory());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }

    private String normalize(String str) {
        return (str == null || str.trim().isEmpty()) ? null : str.trim();
    }
}
