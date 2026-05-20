package org.example.thuvien.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.SoftDelete;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@SoftDelete(columnName = "is_deleted")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String phoneNumber;

    @NotBlank(message = "Căn cước công dân không được để trống")
    @Column(unique = true) // Đảm bảo không trùng CCCD
    private String identityCard;

    // Các trường dành cho Admin đăng nhập
    // Với người dùng bình thường, Admin có thể để trống hoặc hệ thống tự sinh
    @Column(unique = true)
    private String username;

    private String password;

    private String role; // "ADMIN" hoặc "PATRON" (Người mượn)

    // Quan hệ 1-N: Một User có thể có nhiều bản ghi mượn sách
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BorrowRecord> borrowRecords;
}