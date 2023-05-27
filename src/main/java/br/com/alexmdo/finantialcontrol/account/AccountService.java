package br.com.alexmdo.finantialcontrol.account;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        var account = getAccountById(id);
        if (account.isArchived()) {
            accountRepository.delete(account);
        } else {
            throw new IllegalArgumentException("Cannot delete account. Archive it first.");
        }
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    public Account archiveAccount(Long id) {
        var account = getAccountById(id);
        account.setArchived(true);
        return accountRepository.save(account);
    }

    public Account updateInitialAmount(Long id, BigDecimal amount) {
        var account = getAccountById(id);
        account.setInitialAmount(amount);
        return accountRepository.save(account);
    }

    public Page<Account> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

}
