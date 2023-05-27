package br.com.alexmdo.finantialcontrol.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alexmdo.finantialcontrol.account.dto.AccountCreateRequestDto;
import br.com.alexmdo.finantialcontrol.account.dto.AccountDto;
import br.com.alexmdo.finantialcontrol.account.dto.AccountUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountCreateRequestDto createRequestDto) {
        var account = accountMapper.toEntity(createRequestDto);
        var createdAccount = accountService.createAccount(account);
        var accountDto = accountMapper.toDto(createdAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable("id") Long id,
            @Valid @RequestBody AccountUpdateRequestDto updateRequestDto) {
        var existingAccount = accountService.getAccountById(id);
        var updatedAccount = accountMapper.updateEntity(existingAccount, updateRequestDto);
        var savedAccount = accountService.updateAccount(updatedAccount);
        var accountDto = accountMapper.toDto(savedAccount);
        return ResponseEntity.ok(accountDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<AccountDto>> getAccounts(Pageable pageable) {
        var accountPage = accountService.getAllAccounts(pageable);
        var accountDtoPage = accountPage.map(accountMapper::toDto);
        return ResponseEntity.ok(accountDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("id") Long id) {
        var account = accountService.getAccountById(id);
        var accountDto = accountMapper.toDto(account);
        return ResponseEntity.ok(accountDto);
    }

}
