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

    Optional<BorrowRecord> findByBookCopyIdAndReturnDateIsNull(Long bookCopyId);

    long countByReturnDateIsNull();

    List<BorrowRecord> findByReturnDateIsNull();

    long countByUserIdAndReturnDateIsNull(Long userId);

    boolean existsByUserIdAndReturnDateIsNullAndDueDateBefore(Long userId, LocalDate today);

    List<BorrowRecord> findByUserId(Long userId);

    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    long countByStatus(BorrowStatus status);

    List<BorrowRecord> findAllByOrderByBorrowDateDesc();

    @Query("SELECT b FROM BorrowRecord b WHERE b.status IN :statuses AND b.dueDate < :today")
    List<BorrowRecord> findByStatusesAndDueDateBefore(
            @Param("statuses") List<BorrowStatus> statuses,
            @Param("today") LocalDate today);

    @Query("SELECT b FROM BorrowRecord b WHERE b.status = :status AND b.returnDate IS NULL AND b.dueDate <= :date")
    List<BorrowRecord> findByStatusAndReturnDateIsNullAndDueDateLessThanEqual(
            @Param("status") BorrowStatus status,
            @Param("date") LocalDate date);
}
