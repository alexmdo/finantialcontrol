package br.com.alexmdo.finantialcontrol.category.dto;

import br.com.alexmdo.finantialcontrol.category.Category.Type;

public record CategoryDto(Long id, String name, String color, String icon, Type type) {
    // Constructors if needed
}
