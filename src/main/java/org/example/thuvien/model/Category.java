package org.example.thuvien.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(columnName = "is_deleted") // Áp dụng xóa mềm cho cả danh mục
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tên lĩnh vực: CNTT, Văn học, Kinh tế...

    // Quan hệ 1-N: Một lĩnh vực có nhiều cuốn sách
    // mappedBy = "category" vì biến 'category' nằm bên lớp Book sẽ giữ khóa ngoại
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore // Ngăn vòng lặp vô tận
    private List<Book> books;
}