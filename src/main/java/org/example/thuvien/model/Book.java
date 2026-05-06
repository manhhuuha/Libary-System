package org.example.thuvien.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Import cái này
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "books")
@Data
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
}