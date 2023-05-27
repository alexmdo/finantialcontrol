package br.com.alexmdo.finantialcontrol.account.dto;

import java.math.BigDecimal;

import br.com.alexmdo.finantialcontrol.account.AccountType;

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
