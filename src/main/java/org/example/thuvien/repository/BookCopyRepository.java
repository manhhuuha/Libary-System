package org.example.thuvien.repository;

import org.example.thuvien.model.BookCopy;
import org.example.thuvien.model.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    List<BookCopy> findByBookId(Long bookId);

    long countByBookId(Long bookId);

    long countByBookIdAndStatus(Long bookId, BookCopyStatus status);
}
