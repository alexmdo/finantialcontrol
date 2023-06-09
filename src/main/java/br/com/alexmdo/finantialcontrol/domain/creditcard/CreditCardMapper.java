package br.com.alexmdo.finantialcontrol.domain.creditcard;

import br.com.alexmdo.finantialcontrol.domain.account.Account;
import br.com.alexmdo.finantialcontrol.domain.creditcard.dto.CreditCardCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.creditcard.dto.CreditCardDto;
import br.com.alexmdo.finantialcontrol.domain.creditcard.dto.CreditCardUpdateRequestDto;
import org.springframework.stereotype.Component;

@Component
public class CreditCardMapper {

    public CreditCardDto toDto(CreditCard creditCard) {
        return new CreditCardDto(
                creditCard.getId(),
                creditCard.getCreditCardLimit(),
                creditCard.getDescription(),
                creditCard.getBrand(),
                creditCard.getClosingDay(),
                creditCard.getDueDate(),
                creditCard.getAccount().getId(),
                creditCard.isArchived()
        );
    }

    public CreditCard toEntity(CreditCardCreateRequestDto createRequestDto) {
        var account = new Account();
        account.setId(createRequestDto.accountId());
        return new CreditCard(
                null,
                createRequestDto.limit(),
                createRequestDto.description(),
                createRequestDto.brand(),
                createRequestDto.closingDay(),
                createRequestDto.dueDate(),
                false,
                account
        );
    }

    public CreditCard updateEntity(final CreditCard creditCard, final CreditCardUpdateRequestDto updateRequestDto) {
        creditCard.setDescription(updateRequestDto.description());
        creditCard.setAccount(new Account(updateRequestDto.accountId()));
        return creditCard;
    }
}
