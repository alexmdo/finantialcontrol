package br.com.alexmdo.finantialcontrol.domain.creditcard;

import br.com.alexmdo.finantialcontrol.domain.account.Account;
import br.com.alexmdo.finantialcontrol.domain.account.AccountRepository;
import br.com.alexmdo.finantialcontrol.domain.account.AccountType;
import br.com.alexmdo.finantialcontrol.domain.creditcard.dto.CreditCardCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.creditcard.dto.CreditCardUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserRepository;
import br.com.alexmdo.finantialcontrol.util.TestUtil;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CreditCardControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    private User user;
    private CreditCard creditCard;
    private Account account;

    @BeforeEach
    void setUp() {
        creditCardRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        this.user = createNewUser();
        this.account = createNewAccount();
        this.creditCard = createNewCreditCard();

        RestAssured.port = port;
    }

    @Test
    public void testCreateCreditCard() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        CreditCardCreateRequestDto createRequestDto = new CreditCardCreateRequestDto(
                BigDecimal.valueOf(1000),
                "Credit Card Description",
                CreditCardBrand.VISA,
                10,
                25,
                account.getId()
        );

        // Perform POST request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token).body(createRequestDto)
        .when()
            .post("/api/users/me/credit-cards")
        .then()
            .statusCode(201)
            .body("limit", equalTo(1000))
            .body("description", equalTo("Credit Card Description"))
            .body("brand", equalTo("VISA"))
            .body("closingDay", equalTo(10))
            .body("dueDate", equalTo(25))
            .body("accountId", equalTo(account.getId().intValue()))
            .body("archived", equalTo(false));
    }

    @Test
    public void testArchiveCreditCard() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long creditCardId = creditCard.getId();

        // Perform POST request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
        .when()
            .post("/api/users/me/credit-cards/{id}/archive", creditCardId)
        .then()
            .statusCode(200)
            .body("archived", equalTo(true));
    }

    @Test
    public void testDeleteCreditCard() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long creditCardId = creditCard.getId();

        creditCard.setArchived(true);
        creditCardRepository.save(creditCard);

        // Perform DELETE request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/api/users/me/credit-cards/{id}", creditCardId)
        .then()
            .statusCode(204);
    }

    @Test
    public void testUpdateCreditCard() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long creditCardId = creditCard.getId();
        CreditCardUpdateRequestDto updateRequestDto = new CreditCardUpdateRequestDto(
                "Updated Credit Card Description",
                account.getId()
        );

        // Perform PUT request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .body(updateRequestDto)
        .when()
            .put("/api/users/me/credit-cards/{id}", creditCardId)
        .then()
            .statusCode(200)
            .body("description", equalTo("Updated Credit Card Description"))
            .body("accountId", equalTo(account.getId().intValue()));
    }

    @Test
    public void testGetCreditCards() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");

        // Perform GET request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/credit-cards")
        .then()
            .statusCode(200)
            .body("content.size()", equalTo(1));
    }

    @Test
    public void testGetCreditCardById() {
        // Prepare test data
        testCreateCreditCard();
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long creditCardId = 1L;

        // Perform GET request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/credit-cards/{id}", creditCardId)
        .then()
            .statusCode(200)
            .body("id", equalTo(creditCardId.intValue()));
    }

    private User createNewUser() {
        return userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "$2a$10$m9FiHBdOWEgZpnzylyc8ZOHSN5Lbt9qwG7lIJxpeq4KRJwa1oF/Tq"));
    }

    private CreditCard createNewCreditCard() {
        return creditCardRepository.save(new CreditCard(
                null,
                BigDecimal.valueOf(1000),
                "Credit Card Description",
                CreditCardBrand.VISA,
                10,
                25,
                false,
                account));
    }

    private Account createNewAccount() {
        return accountRepository.save(new Account(
                null,
                BigDecimal.valueOf(1000),
                "Bank",
                "Savings Account",
                AccountType.SAVING_ACCOUNT,
                "Blue",
                "piggy-bank",
                false,
                user
        ));
    }

}
