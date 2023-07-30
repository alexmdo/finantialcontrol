package br.com.alexmdo.finantialcontrol.domain.user;

import br.com.alexmdo.finantialcontrol.domain.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.user.dto.UserUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.util.TestUtil;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        this.user = createNewUser("John", "Doe", "johndoe@example.com", "$2a$10$m9FiHBdOWEgZpnzylyc8ZOHSN5Lbt9qwG7lIJxpeq4KRJwa1oF/Tq");

        RestAssured.port = port;
    }

    @Test
    public void testCreateUser() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        UserCreateRequestDto createRequestDto = new UserCreateRequestDto(
                "Jane",
                "Smith",
                "janesmith@example.com",
                "password456"
        );

        // Perform POST request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .body(createRequestDto)
        .when()
            .post("/api/users/me")
        .then()
            .statusCode(201)
            .body("firstName", equalTo("Jane"))
            .body("lastName", equalTo("Smith"))
            .body("email", equalTo("janesmith@example.com"))
            .body("password", nullValue());
    }

    @Test
    public void testGetUserById() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long userId = user.getId();

        // Perform GET request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/{id}", userId)
        .then()
            .statusCode(200)
            .body("id", equalTo(userId.intValue()))
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("email", equalTo("johndoe@example.com"))
            .body("password", nullValue());
    }

    @Test
    public void testUpdateUser() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        Long userId = user.getId();
        UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto(
                "UpdatedFirstName",
                "UpdatedLastName",
                "johndoe@example.com",
                null // Null password means the password is not updated
        );

        // Perform PUT request
        given()
            .port(port)
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .body(updateRequestDto)
        .when()
            .put("/api/users/me/{id}", userId)
        .then()
            .statusCode(200)
            .body("id", equalTo(userId.intValue()))
            .body("firstName", equalTo("UpdatedFirstName"))
            .body("lastName", equalTo("UpdatedLastName"))
            .body("email", equalTo("johndoe@example.com"))
            .body("password", nullValue());
    }

    private User createNewUser(String firstName, String lastName, String email, String password) {
        return userRepository.save(new User(null, firstName, lastName, email, password));
    }
}
