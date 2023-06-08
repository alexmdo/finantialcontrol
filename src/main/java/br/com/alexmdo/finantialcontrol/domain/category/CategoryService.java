package br.com.alexmdo.finantialcontrol.domain.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alexmdo.finantialcontrol.domain.category.exception.CategoryNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
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