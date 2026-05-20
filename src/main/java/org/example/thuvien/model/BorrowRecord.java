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

    @ManyToOne // Nhiều bản ghi mượn có thể thuộc về 1 User
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne // Nhiều bản ghi mượn có thể thuộc về 1 cuốn sách
    @JoinColumn(name = "book_id")
    private Book book;
    @Enumerated(EnumType.STRING)
    private BorrowStatus status;
}