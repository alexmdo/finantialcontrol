package br.com.alexmdo.finantialcontrol.domain.creditcard;

import br.com.alexmdo.finantialcontrol.domain.account.Account;
import br.com.alexmdo.finantialcontrol.domain.account.AccountService;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.infra.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
    @CircuitBreaker(name = "createCreditCard", fallbackMethod = "createCreditCardFallback")
    @TimeLimiter(name = "createCreditCard")
    public CompletableFuture<CreditCard> createCreditCardAsync(CreditCard creditCard, User user) {
        return accountService.getAccountByIdAndUserAsync(creditCard.getAccount().getId(), user)
                .thenApply(account -> {
                    creditCard.setAccount(account);
                    return creditCardRepository.save(creditCard);
                });
    }

    public CompletableFuture<Account> createCreditCardFallback(CreditCard creditCard, User user, Throwable throwable) {
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

}
