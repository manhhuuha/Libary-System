package org.example.thuvien.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.example.thuvien.dto.BookResponseDTO;
import org.example.thuvien.model.Book;
import org.example.thuvien.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping // Trả về danh sách sách khi gọi GET /api/books
    public List<BookResponseDTO> getAll() {
        return bookService.getAllBooks();
    }

    @PostMapping // Lưu sách mới khi gọi POST /api/books
    public Book createBook(@Valid @RequestBody Book book) {
        return bookService.saveBook(book);
    }
    // Lấy chi tiết 1 cuốn sách: GET /api/books/{id}
    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    // API: GET http://localhost:8080/api/books/search?title=Harry
    // API: GET http://localhost:8080/api/books/search?author=Rowling
    @GetMapping("/search")
    public List<BookResponseDTO> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String categoryName) {
        return bookService.searchBooks(title, author, categoryName);
    }

    // Cập nhật sách: PUT /api/books/{id}
    @PutMapping("/{id}")
    public Book update(@PathVariable Long id,@Valid @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    // Xóa sách: DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "Đã xóa thành công sách có ID: " + id;
    }
}