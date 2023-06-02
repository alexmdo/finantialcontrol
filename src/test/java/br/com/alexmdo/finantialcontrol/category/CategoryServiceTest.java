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

import br.com.alexmdo.finantialcontrol.user.User;

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

        var createdCategory = categoryService.createCategory(category);

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

        var updatedCategory = categoryService.updateCategory(category);

        verify(categoryRepository, times(1)).save(category);
        assertEquals(category, updatedCategory);
    }

    @Test
    void testDeleteCategory() {
        var categoryId = 1L;

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void testGetCategoryById() {
        var categoryId = 1L;
        var category = new Category();
        category.setId(categoryId);
        category.setName("Food");
        category.setType(Category.Type.EXPENSE);
        category.setUser(new User(1L, "Joe", "Doe", "johndoe@example.com", "123"));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        var retrievedCategory = categoryService.getCategoryById(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
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

        var retrievedCategory = categoryService.getCategoryByName(categoryName);

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

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        var retrievedCategories = categoryService.getAllCategories(pageable);

        verify(categoryRepository, times(1)).findAll(pageable);
        assertEquals(categoryPage, retrievedCategories);
    }
}
