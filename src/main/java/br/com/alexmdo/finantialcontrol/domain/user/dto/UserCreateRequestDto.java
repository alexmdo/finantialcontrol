package br.com.alexmdo.finantialcontrol.domain.user.dto;

public record UserCreateRequestDto(
        String firstName,
        String lastName,
        String email,
        String password) {
}
