package br.com.alexmdo.finantialcontrol.user.dto;

public record UserCreateRequestDto(
        String firstName,
        String lastName,
        String email) {
}
