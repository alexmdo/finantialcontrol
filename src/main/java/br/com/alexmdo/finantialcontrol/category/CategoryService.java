package br.com.alexmdo.finantialcontrol.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.alexmdo.finantialcontrol.category.exception.CategoryNotFoundException;
import br.com.alexmdo.finantialcontrol.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategoryByUser(Long id, User user) {
        var category = categoryRepository.findByIdAndUser(id, user).orElseThrow(() -> new CategoryNotFoundException("Category not found given the id"));
        categoryRepository.delete(category);
    }

    public Category getCategoryByIdAndUser(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user).orElseThrow(() -> new CategoryNotFoundException("Category not found given the id"));
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(() -> new CategoryNotFoundException("Category not found given the name"));
    }

    public Page<Category> getAllCategoriesByUser(Pageable pageable, User user) {
        return categoryRepository.findAllByUser(pageable, user);
    }

}