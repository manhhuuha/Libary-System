package org.example.thuvien.mapper;

import org.example.thuvien.dto.BookResponseDTO;
import org.example.thuvien.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // Cấu hình Mapping:
    // Lấy giá trị từ thuộc tính 'name' của đối tượng 'category' trong Book
    // Gán vào thuộc tính 'categoryName' của BookResponseDTO
    @Mapping(source = "category.name", target = "categoryName", defaultValue = "Chưa phân loại")
    BookResponseDTO toResponseDTO(Book book);

    // MapStruct tự động hiểu cách convert cả một danh sách
    List<BookResponseDTO> toResponseDTOList(List<Book> books);
}