package org.example.thuvien.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Import cái này
import lombok.Data;
import org.hibernate.annotations.SoftDelete;

import java.util.List;

@Entity
@Table(name = "books")
@Data
@SoftDelete(columnName = "is_deleted")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sách không được để trống")
    @Size(min = 2, max = 100, message = "Tên sách phải từ 2 đến 100 ký tự")
    private String title;

    @NotBlank(message = "Tên tác giả không được để trống")
    private String author;

    @Pattern(regexp = "^[0-9]{10,13}$", message = "ISBN phải là số từ 10 đến 13 chữ số")
    private String isbn;

    @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
    @Max(value = 2025, message = "Năm xuất bản không được lớn hơn năm hiện tại")
    private int publishedYear;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<BorrowRecord> borrowRecords;
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;
    private String location;

    // Quan hệ N-1: Nhiều cuốn sách thuộc về 1 Lĩnh vực
    @ManyToOne(fetch = FetchType.LAZY) // Dùng LAZY để tối ưu hiệu năng (chỉ tải Category khi cần)
    @JoinColumn(name = "category_id") // Tên cột khóa ngoại trong bảng books
    private Category category;
}