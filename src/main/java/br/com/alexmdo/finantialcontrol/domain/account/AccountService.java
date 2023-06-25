package br.com.alexmdo.finantialcontrol.domain.account;

import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotArchivedException;
import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Transactional
    public CompletableFuture<Account> createAccountAsync(Account account) {
        return userService
                .getUserByIdAndUser(account.getUser().getId(), account.getUser())
                .thenApply(userFound -> {
                    account.setUser(userFound);
                    return accountRepository.save(account);
                });
    }

    @Transactional
    public CompletableFuture<Account> updateAccountAsync(Account account) {
        return CompletableFuture
                .supplyAsync(() -> accountRepository.save(account));
    }

    @Transactional
    public CompletableFuture<Void> deleteAccountByUserAsync(Long id, User user) {
        return getAccountByIdAndUserAsync(id, user)
                .thenCompose(account -> {
                    if (account.isArchived()) {
                        return CompletableFuture.runAsync(() -> accountRepository.delete(account));
                    } else {
                        throw new AccountNotArchivedException("Cannot delete account. Archive it first.");
                    }
                });
    }

    public CompletableFuture<Account> getAccountByIdAndUserAsync(Long id, User user) {
        return CompletableFuture
                .supplyAsync(() -> accountRepository.findByIdAndUser(id, user)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id)));
    }

    @Transactional
    public CompletableFuture<Account> archiveAccountForUserAsync(Long id, User user) {
        return getAccountByIdAndUserAsync(id, user)
                .thenCompose(account -> {
                    account.setArchived(true);
                    return CompletableFuture.supplyAsync(() -> accountRepository.save(account));
                });
    }

    @Transactional
    public CompletableFuture<Account> updateInitialAmountForUserAsync(Long id, BigDecimal amount, User user) {
        return getAccountByIdAndUserAsync(id, user)
                .thenCompose(account -> {
                    account.setInitialAmount(amount);
                    return CompletableFuture.supplyAsync(() -> accountRepository.save(account));
                });
    }

    public CompletableFuture<Page<Account>> getAllAccountsByUserAsync(Pageable pageable, User user) {
        return CompletableFuture
                .supplyAsync(() -> accountRepository.findAllByUser(pageable, user));
    }

}
