package br.com.alexmdo.finantialcontrol.domain.creditcard.dto;

import br.com.alexmdo.finantialcontrol.domain.creditcard.CreditCardBrand;

import java.math.BigDecimal;

public record CreditCardCreateRequestDto(
        BigDecimal limit,
        String description,
        CreditCardBrand brand,
        Integer closingDay,
        Integer dueDate,
        Long accountId
) {
}
