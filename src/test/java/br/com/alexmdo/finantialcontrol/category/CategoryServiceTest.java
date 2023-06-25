package br.com.alexmdo.finantialcontrol.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import br.com.alexmdo.finantialcontrol.domain.category.Category;
import br.com.alexmdo.finantialcontrol.domain.category.CategoryRepository;
import br.com.alexmdo.finantialcontrol.domain.category.CategoryService;
import br.com.alexmdo.finantialcontrol.domain.user.User;

@ActiveProfiles("test")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory() {
        var user = new User(1L, "Joe", "Doe", "johndoe@example.com", "123");

        var category = new Category();
        category.setName("Food");
        category.setType(Category.Type.EXPENSE);
        category.setUser(user);

        when(categoryRepository.save(category)).thenReturn(category);

        var createdCategory = categoryService.createCategoryAsync(category);

        verify(categoryRepository, times(1)).save(category);
        assertEquals(category, createdCategory);
    }

    @Test
    void testUpdateCategory() {
        var user = new User(1L, "Joe", "Doe", "johndoe@example.com", "123");

        var category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setType(Category.Type.EXPENSE);
        category.setUser(user);

        when(categoryRepository.save(category)).thenReturn(category);

        var updatedCategory = categoryService.updateCategoryAsync(category);

        verify(categoryRepository, times(1)).save(category);
        assertEquals(category, updatedCategory);
    }

    @Test
    void testDeleteCategory() {
        var user = new User(1L, "Joe", "Doe", "joe@doe.com", "123");
        var category = new Category(1L, "Food", "red", "food-icon", Category.Type.EXPENSE, user);
        
        when(categoryRepository.findByIdAndUser(category.getId(), user)).thenReturn(Optional.of(category));

        categoryService.deleteCategoryByUserAsync(category.getId(), user);

        verify(categoryRepository, times(1)).delete(category);
        verify(categoryRepository, times(1)).findByIdAndUser(category.getId(), user);
    }

    @Test
    void testGetCategoryById() {
        var user = new User(1L, "Joe", "Doe", "joe@doe.com", "123");
        var category = new Category(1L, "Food", "red", "food-icon", Category.Type.EXPENSE, user);
        
        when(categoryRepository.findByIdAndUser(category.getId(), user)).thenReturn(Optional.of(category));

        var retrievedCategory = categoryService.getCategoryByIdAndUserAsync(category.getId(), user);

        verify(categoryRepository, times(1)).findByIdAndUser(category.getId(), user);
        assertEquals(category, retrievedCategory);
    }

    @Test
    void testGetCategoryByName() {
        var categoryName = "Food";
        var category = new Category();
        category.setName(categoryName);
        category.setType(Category.Type.EXPENSE);
        category.setUser(new User(1L, "Joe", "Doe", "johndoe@example.com", "123"));

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));

        var retrievedCategory = categoryService.getCategoryByNameAsync(categoryName);

        verify(categoryRepository, times(1)).findByName(categoryName);
        assertEquals(category, retrievedCategory);
    }

    @Test
    void testGetAllCategories() {
        var user = new User(1L, "Joe", "Doe", "johndoe@example.com", "123");
        var categoryList = Arrays.asList(
                new Category(1L, "Food", null, null, Category.Type.EXPENSE, user),
                new Category(2L, "Salary", null, null, Category.Type.INCOME, user));
        var pageable = mock(Pageable.class);
        var categoryPage = new PageImpl<>(categoryList, pageable, categoryList.size());

        when(categoryRepository.findAllByUser(pageable, user)).thenReturn(categoryPage);

        var retrievedCategories = categoryService.getAllCategoriesByUserAsync(pageable, user);

        verify(categoryRepository, times(1)).findAllByUser(pageable, user);
        assertEquals(categoryPage, retrievedCategories);
    }
}
