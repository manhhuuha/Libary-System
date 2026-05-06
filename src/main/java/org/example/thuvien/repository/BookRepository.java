package org.example.thuvien.repository;

import org.example.thuvien.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Tìm danh sách sách mà tiêu đề chứa từ khóa (không phân biệt hoa thường)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Tìm danh sách sách theo tên tác giả
    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String author, String title);
}