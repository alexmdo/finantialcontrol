package br.com.alexmdo.finantialcontrol.domain.user.dto;

public record UserUpdateRequestDto(
        String firstName,
        String lastName,
        String email,
        String password) {
}
