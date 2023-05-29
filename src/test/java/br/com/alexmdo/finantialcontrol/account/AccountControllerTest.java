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

import br.com.alexmdo.finantialcontrol.account.dto.AccountCreateRequestDto;
import br.com.alexmdo.finantialcontrol.account.dto.AccountUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.user.User;
import br.com.alexmdo.finantialcontrol.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AccountControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
        RestAssured.port = port;
    }

    @Test
    void shouldCreateAccount() {
        var user = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com"));
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
            .body(createRequestDto)
        .when()
            .post("/api/accounts")
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
        var user = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com"));
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
            .body(updateRequestDto)
        .when()
            .put("/api/accounts/{id}", account.getId())
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
        var user = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com"));
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
            .pathParam("id", account.getId())
        .when()
            .delete("/api/accounts/{id}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldGetAccounts() {
        var user = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com"));
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
        .when()
            .get("/api/accounts")
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
        var user = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com"));
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
            .pathParam("id", account.getId())
        .when()
            .get("/api/accounts/{id}")
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
