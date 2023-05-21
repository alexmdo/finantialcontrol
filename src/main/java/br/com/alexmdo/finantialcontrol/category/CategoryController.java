package br.com.alexmdo.finantialcontrol.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alexmdo.finantialcontrol.category.dto.CategoryCreateRequestDto;
import br.com.alexmdo.finantialcontrol.category.dto.CategoryDto;
import br.com.alexmdo.finantialcontrol.category.dto.CategoryUpdateRequestDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryCreateRequestDto createRequestDto) {
        var category = categoryMapper.toEntity(createRequestDto);
        var createdCategory = categoryService.createCategory(category);
        var categoryDto = categoryMapper.toDto(createdCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryUpdateRequestDto updateRequestDto) {
        var existingCategory = categoryService.getCategoryById(id);
        var updatedCategory = categoryMapper.updateEntity(existingCategory, updateRequestDto);
        var savedCategory = categoryService.updateCategory(updatedCategory);
        var responseDto = categoryMapper.toDto(savedCategory);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CategoryDto>> getCategories(Pageable pageable) {
        var categoryPage = categoryService.getAllCategories(pageable);
        var categoryDtoPage = categoryPage.map(categoryMapper::toDto);
        return ResponseEntity.ok(categoryDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("id") Long id) {
        var category = categoryService.getCategoryById(id);
        var categoryDto = categoryMapper.toDto(category);
        return ResponseEntity.ok(categoryDto);
    }

}
