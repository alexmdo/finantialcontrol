package br.com.alexmdo.finantialcontrol.domain.creditcard;

import br.com.alexmdo.finantialcontrol.domain.account.Account;
import br.com.alexmdo.finantialcontrol.domain.account.AccountService;
import br.com.alexmdo.finantialcontrol.domain.account.AccountType;
import br.com.alexmdo.finantialcontrol.domain.creditcard.exception.CreditCardNotArchivedException;
import br.com.alexmdo.finantialcontrol.domain.creditcard.exception.CreditCardNotFoundException;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditCardServiceTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private CreditCardService creditCardService;

    @Captor
    private ArgumentCaptor<CreditCard> creditCardCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCreditCardForUserAsync_ValidInput_CreatesCreditCardWithAccount() {
        // Arrange
        CreditCard creditCard = new CreditCard();
        creditCard.setId(1L);

        User user = new User();
        user.setEmail("john");

        Account account = new Account();
        account.setId(1L);

        // Set the account for the credit card
        creditCard.setAccount(account);

        when(accountService.getAccountByIdAndUserAsync(creditCard.getAccount().getId(), user))
                .thenReturn(CompletableFuture.completedFuture(account));
        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);

        // Act
        CompletableFuture<CreditCard> futureCreditCard = creditCardService.createCreditCardForUserAsync(creditCard, user);

        // Assert
        assertDoesNotThrow(futureCreditCard::join);
        verify(accountService).getAccountByIdAndUserAsync(creditCard.getAccount().getId(), user);
        verify(creditCardRepository).save(creditCardCaptor.capture());
        assertEquals(account, creditCardCaptor.getValue().getAccount());
    }


    @Test
    void archiveCreditCardForUserAsync_CreditCardExists_ArchivesCreditCard() {
        // Arrange
        Long creditCardId = 1L;
        User user = new User();
        user.setEmail("john");

        CreditCard existingCreditCard = new CreditCard();
        existingCreditCard.setId(creditCardId);

        when(creditCardRepository.findByIdAndAccountUser(creditCardId, user)).thenReturn(Optional.of(existingCreditCard));
        when(creditCardRepository.save(existingCreditCard)).thenReturn(existingCreditCard);

        // Act
        CompletableFuture<CreditCard> futureCreditCard = creditCardService.archiveCreditCardForUserAsync(creditCardId, user);

        // Assert
        assertDoesNotThrow(futureCreditCard::join);
        verify(creditCardRepository).findByIdAndAccountUser(creditCardId, user);
        verify(creditCardRepository).save(creditCardCaptor.capture());
        assertTrue(creditCardCaptor.getValue().isArchived());
    }

    @Test
    void archiveCreditCardForUserAsync_CreditCardNotFound_ThrowsCreditCardNotFoundException() {
        // Arrange
        Long creditCardId = 1L;
        User user = new User();
        user.setEmail("john");

        when(creditCardRepository.findByIdAndAccountUser(creditCardId, user)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<CreditCard> futureCreditCard = creditCardService.archiveCreditCardForUserAsync(creditCardId, user);

        // Assert
        CompletionException completionException = assertThrows(CompletionException.class, futureCreditCard::join);
        Throwable actualException = completionException.getCause();
        assertTrue(actualException instanceof CreditCardNotFoundException);
        assertEquals("Credit card not found with id '1' and user 'john'", actualException.getMessage());
    }


    @Test
    void deleteCreditCardForUserAsync_CreditCardArchived_DeletesCreditCard() {
        // Arrange
        Long creditCardId = 1L;
        User user = new User();
        user.setEmail("john");

        CreditCard existingCreditCard = new CreditCard();
        existingCreditCard.setId(creditCardId);
        existingCreditCard.setArchived(true);

        when(creditCardRepository.findByIdAndAccountUser(creditCardId, user)).thenReturn(Optional.of(existingCreditCard));

        // Act
        CompletableFuture<Void> future = creditCardService.deleteCreditCardForUserAsync(creditCardId, user);

        // Assert
        assertDoesNotThrow(future::join);
        verify(creditCardRepository).delete(existingCreditCard);
    }

    @Test
    void deleteCreditCardForUserAsync_CreditCardNotArchived_ThrowsCreditCardNotArchivedException() {
        // Arrange
        Long creditCardId = 1L;
        User user = new User();
        user.setEmail("john");

        CreditCard existingCreditCard = new CreditCard();
        existingCreditCard.setId(creditCardId);
        existingCreditCard.setArchived(false);

        when(creditCardRepository.findByIdAndAccountUser(creditCardId, user)).thenReturn(Optional.of(existingCreditCard));

        // Act
        CompletableFuture<Void> future = creditCardService.deleteCreditCardForUserAsync(creditCardId, user);

        // Assert
        CompletionException completionException = assertThrows(CompletionException.class, future::join);
        Throwable cause = completionException.getCause();
        assertTrue(cause instanceof CreditCardNotArchivedException);
        assertEquals("Cannot delete credit card. Archive it first.", cause.getMessage());
        verify(creditCardRepository, never()).delete(existingCreditCard);
    }


    @Test
    void getCreditCardByIdAndUserAsync_CreditCardExists_ReturnsCreditCard() {
        // Arrange
        Long creditCardId = 1L;
        User user = new User();
        user.setEmail("john");

        CreditCard existingCreditCard = new CreditCard();
        existingCreditCard.setId(creditCardId);

        when(creditCardRepository.findByIdAndAccountUser(creditCardId, user)).thenReturn(Optional.of(existingCreditCard));

        // Act
        CompletableFuture<CreditCard> futureCreditCard = creditCardService.getCreditCardByIdAndUserAsync(creditCardId, user);

        // Assert
        assertEquals(existingCreditCard, futureCreditCard.join());
        verify(creditCardRepository).findByIdAndAccountUser(creditCardId, user);
    }

    @Test
    void getCreditCardByIdAndUserAsync_CreditCardNotFound_ThrowsCreditCardNotFoundException() {
        // Arrange
        Long creditCardId = 1L;
        User user = new User();
        user.setEmail("john");

        when(creditCardRepository.findByIdAndAccountUser(creditCardId, user)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<CreditCard> futureCreditCard = creditCardService.getCreditCardByIdAndUserAsync(creditCardId, user);

        // Assert
        CompletionException exception = assertThrows(CompletionException.class, futureCreditCard::join);
        assertTrue(exception.getCause() instanceof CreditCardNotFoundException);
        assertEquals("Credit card not found with id '1' and user 'john'", exception.getCause().getMessage());
    }


    @Test
    void updateCreditCardAsync_ValidInput_UpdatesCreditCard() {
        // Arrange
        CreditCard updatedCreditCard = new CreditCard();
        updatedCreditCard.setId(1L);

        when(creditCardRepository.save(updatedCreditCard)).thenReturn(updatedCreditCard);

        // Act
        CompletableFuture<CreditCard> futureCreditCard = creditCardService.updateCreditCardAsync(updatedCreditCard);

        // Assert
        assertEquals(updatedCreditCard, futureCreditCard.join());
        verify(creditCardRepository).save(updatedCreditCard);
    }

    @Test
    void getAllCreditCardsByUserAsync_ValidInput_ReturnsPageOfCreditCards() {
        // Arrange
        User user = new User();
        user.setEmail("john");
        Pageable pageable = Pageable.unpaged();

        Page<CreditCard> creditCardPage = mock(Page.class);

        when(creditCardRepository.findAllByAccountUser(pageable, user)).thenReturn(creditCardPage);

        // Act
        CompletableFuture<Page<CreditCard>> futurePage = creditCardService.getAllCreditCardsByUserAsync(pageable, user);

        // Assert
        assertEquals(creditCardPage, futurePage.join());
        verify(creditCardRepository).findAllByAccountUser(pageable, user);
    }

    // Additional tests for fallback methods can be added if required.
}