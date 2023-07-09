package br.com.alexmdo.finantialcontrol.domain.account;

import br.com.alexmdo.finantialcontrol.domain.account.Account;
import br.com.alexmdo.finantialcontrol.domain.account.AccountRepository;
import br.com.alexmdo.finantialcontrol.domain.account.AccountService;
import br.com.alexmdo.finantialcontrol.domain.account.AccountType;
import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotArchivedException;
import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;
    
    @InjectMocks
    private AccountService accountService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_ValidInput_ReturnsCreatedAccount() {
        // Arrange
        var account = new Account();
        var user = new User(1L, "John", "Doe", "john@doe.com", "123456");
        account.setUser(user);
        when(accountRepository.save(account)).thenReturn(account);
        when(userService.getUserByIdAndUserAsync(1L, user)).thenReturn(CompletableFuture.completedFuture(user));

        // Act
        var createdAccountFuture = accountService.createAccountAsync(account);

        // Assert
        assertDoesNotThrow(createdAccountFuture::join);
        var createdAccount = createdAccountFuture.join();
        assertNotNull(createdAccount);
        assertEquals(account, createdAccount);
        verify(accountRepository, times(1)).save(account);
    }


    @Test
    void updateAccount_ValidInput_ReturnsUpdatedAccount() {
        // Arrange
        var accountId = 1L;
        var existingAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", false, null);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        // Act
        var updatedAccountFuture = accountService.updateAccountAsync(existingAccount);

        // Assert
        assertDoesNotThrow(updatedAccountFuture::join);
        var updatedAccount = updatedAccountFuture.join();
        assertNotNull(updatedAccount);
        assertEquals(existingAccount, updatedAccount);
        verify(accountRepository, times(1)).save(existingAccount);
    }


    @Test
    void deleteAccount_ArchivedAccount_DeletesAccount() {
        // Arrange
        var accountId = 1L;
        var user = new User(1L, "Joe", "Doe", "johndoe@example.com", "123");
        var archivedAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", true, user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(archivedAccount));

        // Act
        var deleteAccountFuture = accountService.deleteAccountByUserAsync(accountId, user);

        // Assert
        assertDoesNotThrow(deleteAccountFuture::join);
        verify(accountRepository, times(1)).delete(archivedAccount);
    }


    @Test
    void deleteAccount_NonArchivedAccount_ThrowsIllegalArgumentException() {
        // Arrange
        var accountId = 1L;
        var user = new User(1L, "Joe", "Doe", "johndoe@example.com", "123");
        var nonArchivedAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", false, null);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(nonArchivedAccount));

        // Act and Assert
        assertThrows(CompletionException.class, () -> accountService.deleteAccountByUserAsync(accountId, user).join());
        verify(accountRepository, times(0)).delete(any());
    }


    @Test
    void getAccountById_ExistingAccountId_ReturnsAccount() {
        // Arrange
        var accountId = 1L;
        var user = new User(1L, "Joe", "Doe", "johndoe@example.com", "123");
        var existingAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", false, null);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(existingAccount));

        // Act
        var retrievedAccountFuture = accountService.getAccountByIdAndUserAsync(accountId, user);

        // Assert
        assertDoesNotThrow(retrievedAccountFuture::join);
        var retrievedAccount = retrievedAccountFuture.join();
        assertNotNull(retrievedAccount);
        assertEquals(existingAccount, retrievedAccount);
    }


    @Test
    void getAccountById_NonExistingAccountId_ThrowsIllegalArgumentException() {
        // Arrange
        var nonExistingAccountId = 1L;
        var user = new User(1L, "Joe", "Doe", "johndoe@example.com", "123");
        when(accountRepository.findByIdAndUser(nonExistingAccountId, user)).thenReturn(Optional.empty());

        // Act and Assert
        var exception = assertThrows(CompletionException.class,
                () -> accountService.getAccountByIdAndUserAsync(nonExistingAccountId, user).join());
        assertTrue(exception.getCause() instanceof AccountNotFoundException);
    }


    // ... write additional test cases for other methods

}
