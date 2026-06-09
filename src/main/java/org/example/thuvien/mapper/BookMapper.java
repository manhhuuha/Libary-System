package org.example.thuvien.mapper;

import org.example.thuvien.dto.BookResponseDTO;
import org.example.thuvien.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "category.name", target = "categoryName", defaultValue = "Chưa phân loại")
    BookResponseDTO toResponseDTO(Book book);

    List<BookResponseDTO> toResponseDTOList(List<Book> books);
}
