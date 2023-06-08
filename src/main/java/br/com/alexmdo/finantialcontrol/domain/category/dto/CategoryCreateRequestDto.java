package br.com.alexmdo.finantialcontrol.domain.category.dto;

import br.com.alexmdo.finantialcontrol.domain.category.Category;

public record CategoryCreateRequestDto(String name, String color, String icon, Category.Type type, Long userId) {
    // Constructors if needed
}