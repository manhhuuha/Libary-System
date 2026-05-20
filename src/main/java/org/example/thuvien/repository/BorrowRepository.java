package org.example.thuvien.repository;

import org.example.thuvien.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    // Spring sẽ tự hiểu: SELECT * FROM borrow_records WHERE book_id = ? AND return_date IS NULL
    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);

    long countByReturnDateIsNull();

    long countByUserIdAndReturnDateIsNull(Long userId);

    // Tìm các bản ghi quá hạn của 1 User (chưa trả và ngày hết hạn < hôm nay)
    boolean existsByUserIdAndReturnDateIsNullAndDueDateBefore(Long userId, LocalDate today);

    // Tìm theo ID (Mã thẻ)
    List<BorrowRecord> findByUserId(Long userId);

    // Tìm theo CCCD
    List<BorrowRecord> findByUserIdentityCard(String identityCard);

    // Tìm theo Họ tên (Tìm kiếm gần đúng)
    List<BorrowRecord> findByUserFullNameContainingIgnoreCase(String fullName);
}