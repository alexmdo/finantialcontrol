package br.com.alexmdo.finantialcontrol.category.dto;

import br.com.alexmdo.finantialcontrol.category.Category;

public record CategoryUpdateRequestDto(String name, String color, String icon, Category.Type type) {
    // Constructors if needed
}