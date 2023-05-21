package br.com.alexmdo.finantialcontrol.category;

import org.springframework.stereotype.Component;

import br.com.alexmdo.finantialcontrol.category.dto.CategoryCreateRequestDto;
import br.com.alexmdo.finantialcontrol.category.dto.CategoryDto;
import br.com.alexmdo.finantialcontrol.category.dto.CategoryUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.user.User;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getIcon(),
                category.getType());
    }

    public Category toEntity(CategoryCreateRequestDto createRequestDto) {
        return new Category(
                null,
                createRequestDto.name(),
                createRequestDto.color(),
                createRequestDto.icon(),
                createRequestDto.type(),
                new User(createRequestDto.userId(), null, null, null));
    }

    public Category updateEntity(Category existingCategory, CategoryUpdateRequestDto updateRequestDto) {
        return new Category(
                existingCategory.getId(),
                updateRequestDto.name(),
                updateRequestDto.color(),
                updateRequestDto.icon(),
                updateRequestDto.type(),
                existingCategory.getUser());
    }

}
