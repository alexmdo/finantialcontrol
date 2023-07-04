package br.com.alexmdo.finantialcontrol.domain.creditcard.dto;

public record CreditCardUpdateRequestDto(
        String description,
        Long accountId
) {
}
