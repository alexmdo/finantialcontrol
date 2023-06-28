package br.com.alexmdo.finantialcontrol.domain.account;

import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotArchivedException;
import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import br.com.alexmdo.finantialcontrol.infra.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Transactional
    @CircuitBreaker(name = "createAccount", fallbackMethod = "createAccountFallback")
    @TimeLimiter(name = "createAccount")
    public CompletableFuture<Account> createAccountAsync(Account account) {
        return userService
                .getUserByIdAndUserAsync(account.getUser().getId(), account.getUser())
                .thenApply(existingAccount -> {
                    account.setUser(existingAccount);
                    return accountRepository.save(account);
                });
    }

    @Transactional
    @CircuitBreaker(name = "updateAccount", fallbackMethod = "updateAccountFallback")
    @TimeLimiter(name = "updateAccount")
    public CompletableFuture<Account> updateAccountAsync(Account account) {
        return CompletableFuture
                .supplyAsync(() -> accountRepository.save(account));
    }

    @Transactional
    @CircuitBreaker(name = "deleteAccountByUser", fallbackMethod = "deleteAccountByUserFallback")
    @TimeLimiter(name = "deleteAccountByUser")
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

    @CircuitBreaker(name = "getAccountByIdAndUser", fallbackMethod = "getAccountByIdAndUserFallback")
    @TimeLimiter(name = "getAccountByIdAndUser")
    public CompletableFuture<Account> getAccountByIdAndUserAsync(Long id, User user) {
        return CompletableFuture
                .supplyAsync(() -> accountRepository.findByIdAndUser(id, user)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id)));
    }

    @Transactional
    @CircuitBreaker(name = "archiveAccountForUser", fallbackMethod = "archiveAccountForUserFallback")
    @TimeLimiter(name = "archiveAccountForUser")
    public CompletableFuture<Account> archiveAccountForUserAsync(Long id, User user) {
        return getAccountByIdAndUserAsync(id, user)
                .thenCompose(existingAccount -> {
                    existingAccount.setArchived(true);
                    return CompletableFuture.supplyAsync(() -> accountRepository.save(existingAccount));
                });
    }

    @CircuitBreaker(name = "getAllAccountsByUser", fallbackMethod = "getAllAccountsByUserFallback")
    @TimeLimiter(name = "getAllAccountsByUser")
    public CompletableFuture<Page<Account>> getAllAccountsByUserAsync(Pageable pageable, User user) {
        return CompletableFuture
                .supplyAsync(() -> accountRepository.findAllByUser(pageable, user));
    }

    public CompletableFuture<Account> createAccountFallback(Account account, Throwable throwable) {
        // Fallback logic for createAccountAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for createAccountAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Account> updateAccountFallback(Account account, Throwable throwable) {
        // Fallback logic for updateAccountAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for updateAccountAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Void> deleteAccountByUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for deleteAccountByUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for deleteAccountByUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Account> getAccountByIdAndUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for getAccountByIdAndUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getAccountByIdAndUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Account> archiveAccountForUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for archiveAccountForUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for archiveAccountForUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Page<Account>> getAllAccountsByUserFallback(Pageable pageable, User user, Throwable throwable) {
        // Fallback logic for getAllAccountsByUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getAllAccountsByUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

}
