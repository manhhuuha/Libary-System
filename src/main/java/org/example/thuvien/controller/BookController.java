package org.example.thuvien.controller;

import jakarta.validation.Valid;
import org.example.thuvien.dto.BookResponseDTO;
import org.example.thuvien.model.Book;
import org.example.thuvien.model.BookCopy;
import org.example.thuvien.repository.BookCopyRepository;
import org.example.thuvien.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @GetMapping
    public Page<BookResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String categoryName) {
        return bookService.getBooksPaged(page, size, title, author, categoryName);
    }

    @PostMapping
    public Book createBook(@Valid @RequestBody Book book) {
        return bookService.saveBook(book);
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/{id}/copies")
    public List<BookCopy> getCopies(@PathVariable Long id) {
        return bookCopyRepository.findByBookId(id);
    }

    @PostMapping("/{id}/copies")
    public List<BookCopy> addCopies(@PathVariable Long id, @RequestBody Map<String, List<String>> body) {
        return bookService.addCopies(id, body.get("isbns"));
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "Đã xóa thành công sách có ID: " + id;
    }
}
