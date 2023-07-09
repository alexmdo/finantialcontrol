package br.com.alexmdo.finantialcontrol.domain.account;

import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.account.dto.AccountUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserRepository;
import br.com.alexmdo.finantialcontrol.util.TestUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        accountRepository.deleteAll();

        this.user = createNewUser();
        this.account = createNewAccount();

        RestAssured.port = port;
    }

    @Test
    public void testCreateAccount() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        AccountCreateRequestDto createRequestDto = new AccountCreateRequestDto(
                BigDecimal.valueOf(1000),
                "Bank",
                "Savings Account",
                AccountType.SAVING_ACCOUNT,
                "Blue",
                "piggy-bank",
                this.user.getId()
        );

        // Perform POST request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(createRequestDto)
        .when()
            .post("/api/users/me/accounts")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("initialAmount", equalTo(1000))
            .body("financialInstitution", equalTo("Bank"))
            .body("description", equalTo("Savings Account"))
            .body("accountType", equalTo("SAVING_ACCOUNT"))
            .body("color", equalTo("Blue"))
            .body("icon", equalTo("piggy-bank"));
    }

    @Test
    public void testUpdateAccount() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long accountId = account.getId();
        AccountUpdateRequestDto updateRequestDto = new AccountUpdateRequestDto(
                "Updated Bank",
                "Updated Savings Account",
                AccountType.CHECKING_ACCOUNT,
                "Red",
                "piggy-bank"
        );

        // Perform PUT request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(updateRequestDto)
        .when()
            .put("/api/users/me/accounts/{id}", accountId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("financialInstitution", equalTo("Updated Bank"))
            .body("description", equalTo("Updated Savings Account"));
    }

    @Test
    public void testDeleteAccount() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long accountId = account.getId();

        account.setArchived(true);
        accountRepository.save(account);

        // Perform DELETE request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/api/users/me/accounts/{id}", accountId)
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testGetAccounts() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");

        // Perform GET request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/accounts")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(1))
            .body("content[0].initialAmount", equalTo(1000.0F))
            .body("content[0].financialInstitution", equalTo("Bank"))
            .body("content[0].description", equalTo("Savings Account"))
            .body("content[0].accountType", equalTo("SAVING_ACCOUNT"))
            .body("content[0].color", equalTo("Blue"))
            .body("content[0].icon", equalTo("piggy-bank"))
            .body("content[0].isArchived", equalTo(false));
    }

    @Test
    public void testGetAccountById() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long accountId = account.getId();

        // Perform GET request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/accounts/{id}", accountId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(accountId.intValue()))
            .body("initialAmount", equalTo(1000.0F))
            .body("financialInstitution", equalTo("Bank"))
            .body("description", equalTo("Savings Account"))
            .body("accountType", equalTo("SAVING_ACCOUNT"))
            .body("color", equalTo("Blue"))
            .body("icon", equalTo("piggy-bank"))
            .body("isArchived", equalTo(false));
    }

    private User createNewUser() {
        return userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "$2a$10$m9FiHBdOWEgZpnzylyc8ZOHSN5Lbt9qwG7lIJxpeq4KRJwa1oF/Tq"));
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
