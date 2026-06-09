package org.example.thuvien.repository;

import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.model.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);

    long countByReturnDateIsNull();

    List<BorrowRecord> findByReturnDateIsNull();

    long countByUserIdAndReturnDateIsNull(Long userId);

    boolean existsByUserIdAndReturnDateIsNullAndDueDateBefore(Long userId, LocalDate today);

    List<BorrowRecord> findByUserId(Long userId);

    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    List<BorrowRecord> findByUserIdentityCard(String identityCard);

    List<BorrowRecord> findByUserFullNameContainingIgnoreCase(String fullName);

    long countByStatus(BorrowStatus status);

    @Query("SELECT b FROM BorrowRecord b WHERE b.status = :status AND b.dueDate BETWEEN :start AND :end")
    List<BorrowRecord> findByStatusAndDueDateBetween(
            @Param("status") BorrowStatus status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT b FROM BorrowRecord b WHERE b.status = :status AND b.dueDate < :today")
    List<BorrowRecord> findByStatusAndDueDateBefore(
            @Param("status") BorrowStatus status,
            @Param("today") LocalDate today);

    @Query("SELECT b FROM BorrowRecord b WHERE b.status = :status AND b.returnDate IS NULL AND b.dueDate <= :date")
    List<BorrowRecord> findByStatusAndReturnDateIsNullAndDueDateLessThanEqual(
            @Param("status") BorrowStatus status,
            @Param("date") LocalDate date);

    List<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status);

    @Query("SELECT COALESCE(SUM(b.totalQuantity), 0) FROM Book b")
    long sumTotalQuantity();
}
