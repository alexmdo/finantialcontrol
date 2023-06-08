package br.com.alexmdo.finantialcontrol.domain.account;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotArchivedException;
import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Transactional
    public Account createAccount(Account account) {
        var userFound = userService.getUserByIdAndUser(account.getUser().getId(), account.getUser());
        account.setUser(userFound);
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccountByUser(Long id, User user) {
        var account = getAccountByIdAndUser(id, user);
        if (account.isArchived()) {
            accountRepository.delete(account);
        } else {
            throw new AccountNotArchivedException("Cannot delete account. Archive it first.");
        }
    }

    public Account getAccountByIdAndUser(Long id, User user) {
        return accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }

    @Transactional
    public Account archiveAccountForUser(Long id, User user) {
        var account = getAccountByIdAndUser(id, user);
        account.setArchived(true);
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateInitialAmountForUser(Long id, BigDecimal amount, User user) {
        var account = getAccountByIdAndUser(id, user);
        account.setInitialAmount(amount);
        return accountRepository.save(account);
    }

    public Page<Account> getAllAccountsByUser(Pageable pageable, User user) {
        return accountRepository.findAllByUser(pageable, user);
    }

}
