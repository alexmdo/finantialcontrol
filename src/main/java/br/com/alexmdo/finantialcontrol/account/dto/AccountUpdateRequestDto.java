package br.com.alexmdo.finantialcontrol.account.dto;

import br.com.alexmdo.finantialcontrol.account.AccountType;

public record AccountUpdateRequestDto(
        String financialInstitution,
        String description,
        AccountType accountType,
        String color,
        String icon
) {
}
