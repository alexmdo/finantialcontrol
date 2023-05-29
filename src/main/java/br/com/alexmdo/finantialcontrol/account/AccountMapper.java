package br.com.alexmdo.finantialcontrol.account;

import org.springframework.stereotype.Component;

import br.com.alexmdo.finantialcontrol.account.dto.AccountCreateRequestDto;
import br.com.alexmdo.finantialcontrol.account.dto.AccountDto;
import br.com.alexmdo.finantialcontrol.account.dto.AccountUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.user.User;
import br.com.alexmdo.finantialcontrol.user.dto.UserDto;

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
                account.isArchived(),
                UserDto.fromEntity(account.getUser())
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
                new User(createRequestDto.userId(), null, null, null) // User will be set separately
        );
    }

    public Account updateEntity(Account account, AccountUpdateRequestDto updateRequestDto) {
        account.setFinancialInstitution(updateRequestDto.financialInstitution());
        account.setDescription(updateRequestDto.description());
        account.setAccountType(updateRequestDto.accountType());
        account.setColor(updateRequestDto.color());
        account.setIcon(updateRequestDto.icon());

        return account;
    }

}
