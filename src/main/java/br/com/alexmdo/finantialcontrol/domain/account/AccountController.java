package br.com.alexmdo.finantialcontrol.domain.account;

import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountDto;
import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.infra.BaseController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users/me/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class AccountController extends BaseController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public CompletableFuture<ResponseEntity<AccountDto>> createAccountAsync(@Valid @RequestBody AccountCreateRequestDto createRequestDto) {
        var account = accountMapper.toEntity(createRequestDto);
        return accountService
                .createAccountAsync(account)
                .thenApply(createdAccount -> {
                    var accountDto = accountMapper.toDto(createdAccount);
                    return ResponseEntity.status(HttpStatus.CREATED).body(accountDto);
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<AccountDto>> updateAccountAsync(
            @PathVariable("id") Long id,
            @Valid @RequestBody AccountUpdateRequestDto updateRequestDto) {
        var user = super.getPrincipal();
        return accountService
                .getAccountByIdAndUserAsync(id, user)
                .thenCompose(existingAccount -> {
                    var updatedAccount = accountMapper.updateEntity(existingAccount, updateRequestDto);
                    return accountService.updateAccountAsync(updatedAccount);
                })
                .thenApply(savedAccount -> {
                    var accountDto = accountMapper.toDto(savedAccount);
                    return ResponseEntity.ok(accountDto);
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteAccountAsync(@PathVariable("id") Long id) {
        var user = super.getPrincipal();
        return accountService
                .getAccountByIdAndUserAsync(id, user)
                .thenCompose(existingAccount -> accountService.deleteAccountByUserAsync(id, user)
                .thenApply(__ -> ResponseEntity.noContent().build()));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<Page<AccountDto>>> getAccountsAsync(Pageable pageable) {
        var user = super.getPrincipal();
        return accountService
                .getAllAccountsByUserAsync(pageable, user)
                .thenApply(allAccounts -> {
                    var accountDtoPage = allAccounts.map(accountMapper::toDto);
                    return ResponseEntity.ok(accountDtoPage);
                });
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<AccountDto>> getAccountByIdAsync(@PathVariable("id") Long id) {
        var user = super.getPrincipal();
        return accountService
                .getAccountByIdAndUserAsync(id, user)
                .thenApply(account -> {
                    var accountDto = accountMapper.toDto(account);
                    return ResponseEntity.ok(accountDto);
                });
    }

    @PostMapping("/{id}/archive")
    public CompletableFuture<ResponseEntity<AccountDto>> archiveAccountAsync(@PathVariable("id") Long id) {
        var user = super.getPrincipal();
        return accountService
                .archiveAccountForUserAsync(id, user)
                .thenApply(archivedAccount -> {
                    var accountDto = accountMapper.toDto(archivedAccount);
                    return ResponseEntity.ok(accountDto);
                });
    }
}
