package br.com.alexmdo.finantialcontrol.domain.account;


import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotArchivedException;
import br.com.alexmdo.finantialcontrol.domain.account.exception.AccountNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountService accountService;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccountAsync_ValidInput_CreatesAccount() {
        // Arrange
        User user = new User();
        user.setEmail("john");

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);


        when(userService.getUserByIdAndUserAsync(account.getUser().getId(), user))
                .thenReturn(CompletableFuture.completedFuture(user));
        when(accountRepository.save(account)).thenReturn(account);

        // Act
        CompletableFuture<Account> futureAccount = accountService.createAccountAsync(account);

        // Assert
        assertDoesNotThrow(futureAccount::join);
        verify(userService).getUserByIdAndUserAsync(account.getUser().getId(), user);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(user, accountCaptor.getValue().getUser());
    }

    @Test
    void updateAccountAsync_ValidInput_UpdatesAccount() {
        // Arrange
        Account updatedAccount = new Account();
        updatedAccount.setId(1L);

        when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);

        // Act
        CompletableFuture<Account> futureAccount = accountService.updateAccountAsync(updatedAccount);

        // Assert
        assertEquals(updatedAccount, futureAccount.join());
        verify(accountRepository).save(updatedAccount);
    }

    @Test
    void deleteAccountByUserAsync_AccountArchived_DeletesAccount() {
        // Arrange
        Long accountId = 1L;
        User user = new User();
        user.setEmail("john");

        Account existingAccount = new Account();
        existingAccount.setId(accountId);
        existingAccount.setArchived(true);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(existingAccount));

        // Act
        CompletableFuture<Void> future = accountService.deleteAccountByUserAsync(accountId, user);

        // Assert
        assertDoesNotThrow(future::join);
        verify(accountRepository).delete(existingAccount);
    }

    @Test
    void deleteAccountByUserAsync_AccountNotArchived_ThrowsAccountNotArchivedException() {
        // Arrange
        Long accountId = 1L;
        User user = new User();
        user.setEmail("john");

        Account existingAccount = new Account();
        existingAccount.setId(accountId);
        existingAccount.setArchived(false);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(existingAccount));

        // Act
        CompletableFuture<Void> future = accountService.deleteAccountByUserAsync(accountId, user);

        // Assert
        CompletionException completionException = assertThrows(CompletionException.class, future::join);
        Throwable cause = completionException.getCause();
        assertTrue(cause instanceof AccountNotArchivedException);
        assertEquals("Cannot delete account. Archive it first.", cause.getMessage());
        verify(accountRepository, never()).delete(existingAccount);
    }

    @Test
    void getAccountByIdAndUserAsync_AccountExists_ReturnsAccount() {
        // Arrange
        Long accountId = 1L;
        User user = new User();
        user.setEmail("john");

        Account existingAccount = new Account();
        existingAccount.setId(accountId);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(existingAccount));

        // Act
        CompletableFuture<Account> futureAccount = accountService.getAccountByIdAndUserAsync(accountId, user);

        // Assert
        assertEquals(existingAccount, futureAccount.join());
        verify(accountRepository).findByIdAndUser(accountId, user);
    }

    @Test
    void getAccountByIdAndUserAsync_AccountNotFound_ThrowsAccountNotFoundException() {
        // Arrange
        Long accountId = 1L;
        User user = new User();
        user.setEmail("john");

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<Account> futureAccount = accountService.getAccountByIdAndUserAsync(accountId, user);

        // Assert
        CompletionException exception = assertThrows(CompletionException.class, futureAccount::join);
        assertTrue(exception.getCause() instanceof AccountNotFoundException);
        assertEquals("Account not found with id: 1", exception.getCause().getMessage());
    }

    @Test
    void archiveAccountForUserAsync_AccountExists_ArchivesAccount() {
        // Arrange
        Long accountId = 1L;
        User user = new User();
        user.setEmail("john");

        Account existingAccount = new Account();
        existingAccount.setId(accountId);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        // Act
        CompletableFuture<Account> futureAccount = accountService.archiveAccountForUserAsync(accountId, user);

        // Assert
        assertDoesNotThrow(futureAccount::join);
        verify(accountRepository).findByIdAndUser(accountId, user);
        verify(accountRepository).save(accountCaptor.capture());
        assertTrue(accountCaptor.getValue().isArchived());
    }

    @Test
    void archiveAccountForUserAsync_AccountNotFound_ThrowsAccountNotFoundException() {
        // Arrange
        Long accountId = 1L;
        User user = new User();
        user.setEmail("john");

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<Account> futureAccount = accountService.archiveAccountForUserAsync(accountId, user);

        // Assert
        CompletionException completionException = assertThrows(CompletionException.class, futureAccount::join);
        Throwable actualException = completionException.getCause();
        assertTrue(actualException instanceof AccountNotFoundException);
        assertEquals("Account not found with id: 1", actualException.getMessage());
    }

    @Test
    void getAllAccountsByUserAsync_ValidInput_ReturnsPageOfAccounts() {
        // Arrange
        User user = new User();
        user.setEmail("john");
        Pageable pageable = Pageable.unpaged();

        Page<Account> accountPage = mock(Page.class);

        when(accountRepository.findAllByUser(pageable, user)).thenReturn(accountPage);

        // Act
        CompletableFuture<Page<Account>> futurePage = accountService.getAllAccountsByUserAsync(pageable, user);

        // Assert
        assertEquals(accountPage, futurePage.join());
        verify(accountRepository).findAllByUser(pageable, user);
    }

    // Additional tests for fallback methods can be added if required.
}
