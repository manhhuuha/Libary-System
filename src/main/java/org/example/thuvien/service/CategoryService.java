package org.example.thuvien.service;

import org.example.thuvien.model.Book;
import org.example.thuvien.model.Category;
import org.example.thuvien.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getCategorybyId(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Xin lỗi, chúng tôi không tìm thấy Lĩnh vực này! ID: " + id));
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }
    public void delete(Long id){
        Category category = getCategorybyId(id);
        categoryRepository.delete(category);
    }
}
