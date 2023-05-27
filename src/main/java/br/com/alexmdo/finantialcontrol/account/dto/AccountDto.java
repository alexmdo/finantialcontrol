package br.com.alexmdo.finantialcontrol.account.dto;

import java.math.BigDecimal;

import br.com.alexmdo.finantialcontrol.account.AccountType;
import br.com.alexmdo.finantialcontrol.user.dto.UserDto;

public record AccountDto(
        Long id,
        BigDecimal initialAmount,
        String financialInstitution,
        String description,
        AccountType accountType,
        String color,
        String icon,
        Boolean isArchived,
        UserDto user
) {
}
