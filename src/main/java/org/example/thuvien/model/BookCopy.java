package org.example.thuvien.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "book_copies")
@Data
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private String copyNumber;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Enumerated(EnumType.STRING)
    private BookCopyStatus status = BookCopyStatus.AVAILABLE;
}
