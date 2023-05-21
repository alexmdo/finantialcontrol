package br.com.alexmdo.finantialcontrol.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @NotBlank(message = "First name is mandatory") String firstName,
        @NotBlank(message = "Last name is mandatory") String lastName,
        @NotBlank(message = "Email is mandatory") @Email(message = "Invalid email format") String email) {
}
