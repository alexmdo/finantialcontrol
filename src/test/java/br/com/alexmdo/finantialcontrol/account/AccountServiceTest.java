package br.com.alexmdo.finantialcontrol.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_ValidInput_ReturnsCreatedAccount() {
        // Arrange
        Account account = new Account();
        when(accountRepository.save(account)).thenReturn(account);

        // Act
        Account createdAccount = accountService.createAccount(account);

        // Assert
        assertNotNull(createdAccount);
        assertEquals(account, createdAccount);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void updateAccount_ValidInput_ReturnsUpdatedAccount() {
        // Arrange
        Long accountId = 1L;
        Account existingAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", false, null);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        // Act
        Account updatedAccount = accountService.updateAccount(existingAccount);

        // Assert
        assertNotNull(updatedAccount);
        assertEquals(existingAccount, updatedAccount);
        verify(accountRepository, times(1)).save(existingAccount);
    }

    @Test
    void deleteAccount_ArchivedAccount_DeletesAccount() {
        // Arrange
        Long accountId = 1L;
        Account archivedAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", true, null);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(archivedAccount));

        // Act
        accountService.deleteAccount(accountId);

        // Assert
        verify(accountRepository, times(1)).delete(archivedAccount);
    }

    @Test
    void deleteAccount_NonArchivedAccount_ThrowsIllegalArgumentException() {
        // Arrange
        Long accountId = 1L;
        Account nonArchivedAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", false, null);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(nonArchivedAccount));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccount(accountId));
        verify(accountRepository, times(0)).delete(any());
    }

    @Test
    void getAccountById_ExistingAccountId_ReturnsAccount() {
        // Arrange
        Long accountId = 1L;
        Account existingAccount = new Account(accountId, BigDecimal.valueOf(100), "Bank", "Description",
                AccountType.CHECKING_ACCOUNT, "Blue", "Icon", false, null);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));

        // Act
        Account retrievedAccount = accountService.getAccountById(accountId);

        // Assert
        assertNotNull(retrievedAccount);
        assertEquals(existingAccount, retrievedAccount);
    }

    @Test
    void getAccountById_NonExistingAccountId_ThrowsIllegalArgumentException() {
        // Arrange
        Long nonExistingAccountId = 1L;
        when(accountRepository.findById(nonExistingAccountId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(nonExistingAccountId));
    }

    // ... write additional test cases for other methods

}
