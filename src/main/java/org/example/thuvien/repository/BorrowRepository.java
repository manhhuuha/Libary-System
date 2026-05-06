package org.example.thuvien.repository;

import org.example.thuvien.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    // Spring sẽ tự hiểu: SELECT * FROM borrow_records WHERE book_id = ? AND return_date IS NULL
    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);

    // Kiểm tra tồn tại: SELECT COUNT > 0 ...
    boolean existsByBookIdAndReturnDateIsNull(Long bookId);

    long countByReturnDateIsNull();
}