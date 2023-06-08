package br.com.alexmdo.finantialcontrol.domain.user.dto;

import br.com.alexmdo.finantialcontrol.domain.user.User;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email) {

    // Constructors, getters, and setters

    public static UserDto fromEntity(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    // Other methods if needed
}
