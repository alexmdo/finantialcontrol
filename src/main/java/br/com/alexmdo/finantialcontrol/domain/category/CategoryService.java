package br.com.alexmdo.finantialcontrol.domain.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alexmdo.finantialcontrol.domain.category.exception.CategoryNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CompletableFuture<Category> createCategoryAsync(Category category) {
        return CompletableFuture.supplyAsync(() -> categoryRepository.save(category));
    }

    @Transactional
    public CompletableFuture<Category> updateCategoryAsync(Category category) {
        return CompletableFuture.supplyAsync(() -> categoryRepository.save(category));
    }

    @Transactional
    public CompletableFuture<Void> deleteCategoryByUserAsync(Long id, User user) {
        return CompletableFuture
                .supplyAsync(() -> categoryRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() -> new CategoryNotFoundException("Category not found given the id")))
                .thenCompose(category -> {
                    categoryRepository.delete(category);
                    return CompletableFuture.completedFuture(null);
                });
    }

    public CompletableFuture<Category> getCategoryByIdAndUserAsync(Long id, User user) {
        return CompletableFuture.supplyAsync(() -> categoryRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found given the id")));
    }

    public CompletableFuture<Category> getCategoryByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> categoryRepository
                .findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found given the name")));
    }

    public CompletableFuture<Page<Category>> getAllCategoriesByUserAsync(Pageable pageable, User user) {
        return CompletableFuture.supplyAsync(() -> categoryRepository.findAllByUser(pageable, user));
    }

}