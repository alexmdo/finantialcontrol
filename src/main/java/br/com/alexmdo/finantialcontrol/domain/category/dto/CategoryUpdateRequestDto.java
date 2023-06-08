package br.com.alexmdo.finantialcontrol.domain.category.dto;

import br.com.alexmdo.finantialcontrol.domain.category.Category;

public record CategoryUpdateRequestDto(String name, String color, String icon, Category.Type type) {
    // Constructors if needed
}