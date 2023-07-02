package br.com.alexmdo.finantialcontrol.domain.creditcard;

import br.com.alexmdo.finantialcontrol.domain.account.Account;
import br.com.alexmdo.finantialcontrol.domain.account.AccountService;
import br.com.alexmdo.finantialcontrol.domain.creditcard.exception.CreditCardNotArchivedException;
import br.com.alexmdo.finantialcontrol.domain.creditcard.exception.CreditCardNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.infra.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final AccountService accountService;

    @Transactional
    @CircuitBreaker(name = "createCreditCardForUser", fallbackMethod = "createCreditCardForUserFallback")
    @TimeLimiter(name = "createCreditCardForUser")
    public CompletableFuture<CreditCard> createCreditCardForUserAsync(CreditCard creditCard, User user) {
        return accountService.getAccountByIdAndUserAsync(creditCard.getAccount().getId(), user)
                .thenApply(account -> {
                    creditCard.setAccount(account);
                    return creditCardRepository.save(creditCard);
                });
    }

    @Transactional
    @CircuitBreaker(name = "archiveCreditCardForUser", fallbackMethod = "archiveCreditCardForUserFallback")
    @TimeLimiter(name = "archiveCreditCardForUser")
    public CompletableFuture<CreditCard> archiveCreditCardForUserAsync(Long id, User user) {
        return getCreditCardByIdAndUserAsync(id, user)
                .thenCompose(existingCreditCard -> {
                    existingCreditCard.setArchived(true);
                    return CompletableFuture.supplyAsync(() -> creditCardRepository.save(existingCreditCard));
                });
    }

    @Transactional
    @CircuitBreaker(name = "deleteCreditCardForUser", fallbackMethod = "deleteCreditCardForUserFallback")
    @TimeLimiter(name = "deleteCreditCardForUser")
    public CompletableFuture<Void> deleteCreditCardForUserAsync(Long id, User user) {
        return getCreditCardByIdAndUserAsync(id, user)
                .thenCompose(existingCreditCard -> {
                    if (existingCreditCard.isArchived()) {
                        return CompletableFuture.runAsync(() -> creditCardRepository.delete(existingCreditCard));
                    } else {
                        throw new CreditCardNotArchivedException("Cannot delete credit card. Archive it first.");
                    }
                });
    }

    @CircuitBreaker(name = "getCreditCardByIdAndUser", fallbackMethod = "getCreditCardByIdAndUserFallback")
    @TimeLimiter(name = "getCreditCardByIdAndUser")
    public CompletableFuture<CreditCard> getCreditCardByIdAndUserAsync(Long id, User user) {
        return CompletableFuture
                .supplyAsync(() -> creditCardRepository.findByIdAndAccountUser(id, user)
                        .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found with id '" + id + "' and user '" + user.getUsername() + "'")));
    }

    public CompletableFuture<Account> createCreditCardForUserFallback(CreditCard creditCard, User user, Throwable throwable) {
        // Fallback logic for createAccountAsync
        if (throwable instanceof BusinessException || throwable instanceof ConstraintViolationException) {
            throw (RuntimeException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for createCreditCardAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<CreditCard> archiveCreditCardForUserFallback(CreditCard creditCard, User user, Throwable throwable) {
        // Fallback logic for createAccountAsync
        if (throwable instanceof BusinessException) {
            throw (RuntimeException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for archiveCreditCardForUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<CreditCard> getCreditCardByIdAndUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for createAccountAsync
        if (throwable instanceof BusinessException) {
            throw (RuntimeException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getCreditCardByIdAndUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Void> deleteCreditCardForUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for createAccountAsync
        if (throwable instanceof BusinessException) {
            throw (RuntimeException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for deleteCreditCardForUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

}
