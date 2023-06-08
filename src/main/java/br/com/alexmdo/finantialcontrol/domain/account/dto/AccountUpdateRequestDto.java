package br.com.alexmdo.finantialcontrol.domain.account.dto;

import br.com.alexmdo.finantialcontrol.domain.account.AccountType;

public record AccountUpdateRequestDto(
        String financialInstitution,
        String description,
        AccountType accountType,
        String color,
        String icon
) {
}
