package br.com.alexmdo.finantialcontrol.domain.category;

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

import br.com.alexmdo.finantialcontrol.domain.category.dto.CategoryCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.category.dto.CategoryDto;
import br.com.alexmdo.finantialcontrol.domain.category.dto.CategoryUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.infra.BaseController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users/me/categories")
@SecurityRequirement(name = "bearer-key")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<CategoryDto>> createCategoryAsync(@Valid @RequestBody CategoryCreateRequestDto createRequestDto) {
        var category = categoryMapper.toEntity(createRequestDto);
        return categoryService.createCategoryAsync(category)
                .thenApply(createdCategory -> {
                    var categoryDto = categoryMapper.toDto(createdCategory);
                    return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<CategoryDto>> updateCategoryAsync(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryUpdateRequestDto updateRequestDto) {
        return categoryService.getCategoryByIdAndUserAsync(id, super.getPrincipal())
                .thenCompose(foundCategory -> {
                    var updatedCategory = categoryMapper.updateEntity(foundCategory, updateRequestDto);
                    return categoryService.updateCategoryAsync(updatedCategory);
                })
                .thenApply(updatedCategory -> {
                    var responseDto = categoryMapper.toDto(updatedCategory);
                    return ResponseEntity.ok(responseDto);
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteCategoryAsync(@PathVariable("id") Long id) {
        return categoryService.deleteCategoryByUserAsync(id, super.getPrincipal())
                .thenApply(__ -> ResponseEntity.noContent().build());
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<Page<CategoryDto>>> getCategoriesAsync(Pageable pageable) {
        return categoryService.getAllCategoriesByUserAsync(pageable, super.getPrincipal())
                .thenApply(allCategories -> {
                    var categoryDtoPage = allCategories.map(categoryMapper::toDto);
                    return ResponseEntity.ok(categoryDtoPage);
                });
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<CategoryDto>> getCategoryByIdAsync(@PathVariable("id") Long id) {
        return categoryService.getCategoryByIdAndUserAsync(id, super.getPrincipal())
                .thenApply(foundCategory -> {
                    var categoryDto = categoryMapper.toDto(foundCategory);
                    return ResponseEntity.ok(categoryDto);
                });
    }

}
