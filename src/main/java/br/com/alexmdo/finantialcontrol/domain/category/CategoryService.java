package br.com.alexmdo.finantialcontrol.domain.category;

import br.com.alexmdo.finantialcontrol.domain.category.exception.CategoryAlreadyExistsException;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import br.com.alexmdo.finantialcontrol.infra.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final UserService userService;

    @Transactional
    @CircuitBreaker(name = "createCategory", fallbackMethod = "createCategoryFallback")
    @TimeLimiter(name = "createCategory")
    public CompletableFuture<Category> createCategoryAsync(Category category) {
        return CompletableFuture
                .supplyAsync(() -> categoryRepository.save(category));
    }

    @Transactional
    @CircuitBreaker(name = "updateCategory", fallbackMethod = "updateCategoryFallback")
    @TimeLimiter(name = "updateCategory")
    public CompletableFuture<Category> updateCategoryAsync(Category category) {
        return CompletableFuture.supplyAsync(() -> categoryRepository.save(category));
    }

    @Transactional
    @CircuitBreaker(name = "deleteCategoryByUser", fallbackMethod = "deleteCategoryFallback")
    @TimeLimiter(name = "deleteCategoryByUser")
    public CompletableFuture<Void> deleteCategoryAsync(Long id) {
        return CompletableFuture
                .supplyAsync(() -> categoryRepository
                        .findById(id)
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
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else if (throwable instanceof DataIntegrityViolationException) {
            throw new CategoryAlreadyExistsException("Category with name '" + category.getName() + "' already exists.");
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for createCategoryAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Category> updateCategoryFallback(Category category, Throwable throwable) {
        // Fallback logic for updateCategoryAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for updateCategoryAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Void> deleteCategoryFallback(Long id, Throwable throwable) {
        // Fallback logic for deleteCategoryByUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for deleteCategoryByUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Category> getCategoryByIdAndUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for getCategoryByIdAndUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getCategoryByIdAndUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Category> getCategoryByNameFallback(String name, Throwable throwable) {
        // Fallback logic for getCategoryByNameAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getCategoryByNameAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Page<Category>> getAllCategoriesByUserFallback(Pageable pageable, User user, Throwable throwable) {
        // Fallback logic for getAllCategoriesByUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getAllCategoriesByUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

}
