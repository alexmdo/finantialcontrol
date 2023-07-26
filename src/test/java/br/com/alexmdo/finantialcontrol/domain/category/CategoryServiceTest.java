package br.com.alexmdo.finantialcontrol.domain.category;

import br.com.alexmdo.finantialcontrol.domain.category.exception.CategoryNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategoryAsync_ValidInput_CreatesCategory() {
        // Arrange
        User user = new User();
        user.setEmail("john");

        Category category = new Category();
        category.setId(1L);
        category.setUser(user);

        when(userService.getUserByIdAndUserAsync(category.getUser().getId(), user))
                .thenReturn(CompletableFuture.completedFuture(user));
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        CompletableFuture<Category> futureCategory = categoryService.createCategoryAsync(category);

        // Assert
        assertDoesNotThrow(futureCategory::join);
        verify(categoryRepository).save(categoryCaptor.capture());
        assertEquals(user, categoryCaptor.getValue().getUser());
    }

    @Test
    void updateCategoryAsync_ValidInput_UpdatesCategory() {
        // Arrange
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);

        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        // Act
        CompletableFuture<Category> futureCategory = categoryService.updateCategoryAsync(updatedCategory);

        // Assert
        assertEquals(updatedCategory, futureCategory.join());
        verify(categoryRepository).save(updatedCategory);
    }

    @Test
    void deleteCategoryByUserAsync_CategoryExistsAndArchived_DeletesCategory() {
        // Arrange
        Long categoryId = 1L;
        User user = new User();
        user.setEmail("john");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setUser(user);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        // Act
        CompletableFuture<Void> future = categoryService.deleteCategoryAsync(categoryId);

        // Assert
        assertDoesNotThrow(future::join);
        verify(categoryRepository).delete(existingCategory);
    }

    @Test
    void deleteCategoryByUserAsync_CategoryNotFound_ThrowsCategoryNotFoundException() {
        // Arrange
        Long categoryId = 1L;
        User user = new User();
        user.setEmail("john");

        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<Void> future = categoryService.deleteCategoryAsync(categoryId);

        // Assert
        CompletionException completionException = assertThrows(CompletionException.class, future::join);
        assertTrue(completionException.getCause() instanceof CategoryNotFoundException);
        assertEquals("Category not found given the id", completionException.getCause().getMessage());
    }

    @Test
    void getCategoryByIdAndUserAsync_CategoryExists_ReturnsCategory() {
        // Arrange
        Long categoryId = 1L;
        User user = new User();
        user.setEmail("john");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);

        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(existingCategory));

        // Act
        CompletableFuture<Category> futureCategory = categoryService.getCategoryByIdAndUserAsync(categoryId, user);

        // Assert
        assertEquals(existingCategory, futureCategory.join());
        verify(categoryRepository).findByIdAndUser(categoryId, user);
    }

    @Test
    void getCategoryByIdAndUserAsync_CategoryNotFound_ThrowsCategoryNotFoundException() {
        // Arrange
        Long categoryId = 1L;
        User user = new User();
        user.setEmail("john");

        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<Category> futureCategory = categoryService.getCategoryByIdAndUserAsync(categoryId, user);

        // Assert
        CompletionException completionException = assertThrows(CompletionException.class, futureCategory::join);
        assertTrue(completionException.getCause() instanceof CategoryNotFoundException);
        assertEquals("Category not found given the id", completionException.getCause().getMessage());
    }

    @Test
    void getAllCategoriesByUserAsync_ValidInput_ReturnsPageOfCategories() {
        // Arrange
        User user = new User();
        user.setEmail("john");
        Pageable pageable = Pageable.unpaged();

        Page<Category> categoryPage = mock(Page.class);

        when(categoryRepository.findAllByUser(pageable, user)).thenReturn(categoryPage);

        // Act
        CompletableFuture<Page<Category>> futurePage = categoryService.getAllCategoriesByUserAsync(pageable, user);

        // Assert
        assertEquals(categoryPage, futurePage.join());
        verify(categoryRepository).findAllByUser(pageable, user);
    }

    // Additional tests for fallback methods can be added if required.
}
