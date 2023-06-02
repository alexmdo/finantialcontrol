package br.com.alexmdo.finantialcontrol.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Login is required") String login,
        @NotBlank(message = "Password is required") String password) {
}
