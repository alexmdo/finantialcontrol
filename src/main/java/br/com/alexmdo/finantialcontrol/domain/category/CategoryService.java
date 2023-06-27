package br.com.alexmdo.finantialcontrol.domain.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alexmdo.finantialcontrol.domain.category.exception.CategoryNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @CircuitBreaker(name = "createCategory", fallbackMethod = "createCategoryFallback")
    @TimeLimiter(name = "createCategory")
    public CompletableFuture<Category> createCategoryAsync(Category category) {
        return CompletableFuture.supplyAsync(() -> categoryRepository.save(category));
    }

    @Transactional
    @CircuitBreaker(name = "updateCategory", fallbackMethod = "updateCategoryFallback")
    @TimeLimiter(name = "updateCategory")
    public CompletableFuture<Category> updateCategoryAsync(Category category) {
        return CompletableFuture.supplyAsync(() -> categoryRepository.save(category));
    }

    @Transactional
    @CircuitBreaker(name = "deleteCategoryByUser", fallbackMethod = "deleteCategoryByUserFallback")
    @TimeLimiter(name = "deleteCategoryByUser")
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

    @CircuitBreaker(name = "getCategoryByIdAndUser", fallbackMethod = "getCategoryByIdAndUserFallback")
    @TimeLimiter(name = "getCategoryByIdAndUser")
    public CompletableFuture<Category> getCategoryByIdAndUserAsync(Long id, User user) {
        return CompletableFuture.supplyAsync(() -> categoryRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found given the id")));
    }

    @CircuitBreaker(name = "getCategoryByName", fallbackMethod = "getCategoryByNameFallback")
    @TimeLimiter(name = "getCategoryByName")
    public CompletableFuture<Category> getCategoryByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> categoryRepository
                .findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found given the name")));
    }

    @CircuitBreaker(name = "getAllCategoriesByUser", fallbackMethod = "getAllCategoriesByUserFallback")
    @TimeLimiter(name = "getAllCategoriesByUser")
    public CompletableFuture<Page<Category>> getAllCategoriesByUserAsync(Pageable pageable, User user) {
        return CompletableFuture.supplyAsync(() -> categoryRepository.findAllByUser(pageable, user));
    }

    public CompletableFuture<Category> createCategoryFallback(Category category, Throwable throwable) {
        // Fallback logic for createCategoryAsync
        log.error("Fallback triggered for createCategoryAsync due to: " + throwable.getMessage());
        return CompletableFuture.completedFuture(null); // Return a default or fallback value
    }

    public CompletableFuture<Category> updateCategoryFallback(Category category, Throwable throwable) {
        // Fallback logic for updateCategoryAsync
        log.error("Fallback triggered for updateCategoryAsync due to: " + throwable.getMessage());
        return CompletableFuture.completedFuture(null); // Return a default or fallback value
    }

    public CompletableFuture<Void> deleteCategoryByUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for deleteCategoryByUserAsync
        log.error("Fallback triggered for deleteCategoryByUserAsync due to: " + throwable.getMessage());
        return CompletableFuture.completedFuture(null); // Return a default or fallback value
    }

    public CompletableFuture<Category> getCategoryByIdAndUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for getCategoryByIdAndUserAsync
        log.error("Fallback triggered for getCategoryByIdAndUserAsync due to: " + throwable.getMessage());
        return CompletableFuture.completedFuture(null); // Return a default or fallback value
    }

    public CompletableFuture<Category> getCategoryByNameFallback(String name, Throwable throwable) {
        // Fallback logic for getCategoryByNameAsync
        log.error("Fallback triggered for getCategoryByNameAsync due to: " + throwable.getMessage());
        return CompletableFuture.completedFuture(null); // Return a default or fallback value
    }

    public CompletableFuture<Page<Category>> getAllCategoriesByUserFallback(Pageable pageable, User user, Throwable throwable) {
        // Fallback logic for getAllCategoriesByUserAsync
        log.error("Fallback triggered for getAllCategoriesByUserAsync due to: " + throwable.getMessage());
        return CompletableFuture.completedFuture(Page.empty()); // Return an empty page as fallback
    }

}
