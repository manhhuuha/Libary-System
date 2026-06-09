package org.example.thuvien.repository;

import org.example.thuvien.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR :title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR :author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:categoryName IS NULL OR :categoryName = '' OR LOWER(b.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%')))")
    Page<Book> searchBooksPaged(@Param("title") String title,
                                @Param("author") String author,
                                @Param("categoryName") String categoryName,
                                Pageable pageable);
}
