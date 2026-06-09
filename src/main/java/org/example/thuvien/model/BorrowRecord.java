package org.example.thuvien.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_records")
@Data
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_copy_id")
    private BookCopy bookCopy;

    @Enumerated(EnumType.STRING)
    private BorrowStatus status;

    private Boolean emailSent = false;
}
