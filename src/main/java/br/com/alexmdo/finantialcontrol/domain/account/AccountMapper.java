package br.com.alexmdo.finantialcontrol.domain.account;

import org.springframework.stereotype.Component;

import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountDto;
import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.user.User;

@Component
public class AccountMapper {

    public AccountDto toDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getInitialAmount(),
                account.getFinancialInstitution(),
                account.getDescription(),
                account.getAccountType(),
                account.getColor(),
                account.getIcon(),
                account.isArchived()
        );
    }

    public Account toEntity(AccountCreateRequestDto createRequestDto) {
        return new Account(
                null,
                createRequestDto.initialAmount(),
                createRequestDto.financialInstitution(),
                createRequestDto.description(),
                createRequestDto.accountType(),
                createRequestDto.color(),
                createRequestDto.icon(),
                false, // Default value for isArchived
                new User(createRequestDto.userId(), null, null, null, null) // User will be set separately
        );
    }

    public Account updateEntity(Account account, AccountUpdateRequestDto updateRequestDto) {
        if (updateRequestDto.financialInstitution() != null) {
            account.setFinancialInstitution(updateRequestDto.financialInstitution());
        }

        if (updateRequestDto.description() != null) {
            account.setDescription(updateRequestDto.description());
        }

        if (updateRequestDto.accountType() != null) {
            account.setAccountType(updateRequestDto.accountType());
        }

        if (updateRequestDto.color() != null) {
            account.setColor(updateRequestDto.color());
        }
        
        if (updateRequestDto.icon() != null) {
            account.setIcon(updateRequestDto.icon());
        }

        return account;
    }

}
