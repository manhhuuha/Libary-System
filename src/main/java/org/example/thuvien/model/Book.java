package org.example.thuvien.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
    @Max(value = 2026, message = "Năm xuất bản không được lớn hơn năm hiện tại")
    private int publishedYear;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Transient
    private int numberOfCopies = 1;

    @Transient
    private List<String> copyIsbns;
}
