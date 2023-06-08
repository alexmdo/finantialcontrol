package br.com.alexmdo.finantialcontrol.domain.account.dto;

import java.math.BigDecimal;

import br.com.alexmdo.finantialcontrol.domain.account.AccountType;

public record AccountCreateRequestDto(
        BigDecimal initialAmount,
        String financialInstitution,
        String description,
        AccountType accountType,
        String color,
        String icon,
        Long userId
) {
}
