package br.com.alexmdo.finantialcontrol.user.dto;

public record UserUpdateRequestDto(
        String firstName,
        String lastName,
        String email) {
}
