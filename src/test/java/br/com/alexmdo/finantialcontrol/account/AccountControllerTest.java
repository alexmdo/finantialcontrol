package br.com.alexmdo.finantialcontrol.account;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import br.com.alexmdo.finantialcontrol.account.dto.AccountCreateRequestDto;
import br.com.alexmdo.finantialcontrol.account.dto.AccountUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.user.User;
import br.com.alexmdo.finantialcontrol.user.UserRepository;
import br.com.alexmdo.finantialcontrol.util.TestUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;
    
    private User user;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "$2a$10$m9FiHBdOWEgZpnzylyc8ZOHSN5Lbt9qwG7lIJxpeq4KRJwa1oF/Tq"));
        RestAssured.port = port;
    }

    @Test
    void shouldCreateAccount() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var createRequestDto = new AccountCreateRequestDto(
                BigDecimal.valueOf(1000),
                "Bank",
                "Savings Account",
                AccountType.SAVING_ACCOUNT,
                "Blue",
                "piggy-bank",
                user.getId()
        );

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(createRequestDto)
        .when()
            .post("/api/users/me/accounts")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("initialAmount", equalTo(createRequestDto.initialAmount().intValue()))
            .body("financialInstitution", equalTo(createRequestDto.financialInstitution()))
            .body("description", equalTo(createRequestDto.description()))
            .body("accountType", equalTo(createRequestDto.accountType().toString()))
            .body("color", equalTo(createRequestDto.color()))
            .body("icon", equalTo(createRequestDto.icon()));
    }

    @Test
    void shouldUpdateAccount() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var account = accountRepository.save(new Account(
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

        var updateRequestDto = new AccountUpdateRequestDto(
                "Credit Union",
                "Checking Account",
                AccountType.CHECKING_ACCOUNT,
                "Green",
                "credit-card"
        );

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(updateRequestDto)
        .when()
            .put("/api/users/me/accounts/{id}", account.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("initialAmount", equalTo(account.getInitialAmount().floatValue()))
            .body("financialInstitution", equalTo(updateRequestDto.financialInstitution()))
            .body("description", equalTo(updateRequestDto.description()))
            .body("accountType", equalTo(updateRequestDto.accountType().toString()))
            .body("color", equalTo(updateRequestDto.color()))
            .body("icon", equalTo(updateRequestDto.icon()));
    }

    @Test
    void shouldDeleteAccount() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var account = accountRepository.save(new Account(
                null,
                BigDecimal.valueOf(1000),
                "Bank",
                "Savings Account",
                AccountType.SAVING_ACCOUNT,
                "Blue",
                "piggy-bank",
                true,
                user
        ));

        given()
            .header("Authorization", "Bearer " + token)
            .pathParam("id", account.getId())
        .when()
            .delete("/api/users/me/accounts/{id}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldGetAccounts() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var account1 = accountRepository.save(new Account(
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
        var account2 = accountRepository.save(new Account(
                null,
                BigDecimal.valueOf(2000),
                "Credit Union",
                "Checking Account",
                AccountType.CHECKING_ACCOUNT,
                "Green",
                "credit-card",
                false,
                user
        ));

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/accounts")
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("content.size()", equalTo(2))
            .body("content.initialAmount", hasItems(
                account1.getInitialAmount().floatValue(),
                account2.getInitialAmount().floatValue()
            ))
            .body("content.financialInstitution", hasItems(
                account1.getFinancialInstitution(),
                account2.getFinancialInstitution()
            ))
            .body("content.description", hasItems(
                account1.getDescription(),
                account2.getDescription()
            ))
            .body("content.accountType", hasItems(
                account1.getAccountType().toString(),
                account2.getAccountType().toString()
            ))
            .body("content.color", hasItems(
                account1.getColor(),
                account2.getColor()
            ))
            .body("content.icon", hasItems(
                account1.getIcon(),
                account2.getIcon()
            ));
    }

    @Test
    void shouldGetAccountById() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var account = accountRepository.save(new Account(
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

        given()
            .header("Authorization", "Bearer " + token)
            .pathParam("id", account.getId())
        .when()
            .get("/api/users/me/accounts/{id}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("initialAmount", equalTo(account.getInitialAmount().floatValue()))
            .body("financialInstitution", equalTo(account.getFinancialInstitution()))
            .body("description", equalTo(account.getDescription()))
            .body("accountType", equalTo(account.getAccountType().toString()))
            .body("color", equalTo(account.getColor()))
            .body("icon", equalTo(account.getIcon()));
    }
    
}
