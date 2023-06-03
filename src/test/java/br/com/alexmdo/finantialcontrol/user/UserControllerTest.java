package br.com.alexmdo.finantialcontrol.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import br.com.alexmdo.finantialcontrol.account.TestUtil;
import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserUpdateRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = userRepository.save(new User(null, "John", "Doe", "admin@example.com", "$2a$10$m9FiHBdOWEgZpnzylyc8ZOHSN5Lbt9qwG7lIJxpeq4KRJwa1oF/Tq"));
        RestAssured.port = port;
    }

    @Test
    void shouldCreateUser() {
        var token = TestUtil.authenticate("admin@example.com", "123456");
        var createRequestDto = new UserCreateRequestDto("John", "Doe", "johndoe@example.com", "123456");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(createRequestDto)
        .when()
            .post("/api/users")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("firstName", equalTo(createRequestDto.firstName()))
            .body("lastName", equalTo(createRequestDto.lastName()))
            .body("email", equalTo(createRequestDto.email()));
    }

    @Test
    void shouldUpdateUser() {
        var token = TestUtil.authenticate("admin@example.com", "123456");
        var existingUser = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "123456"));

        var updateRequestDto = new UserUpdateRequestDto("Jane", "Smith", "janesmith@example.com", "123456");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(updateRequestDto)
        .when()
            .put("/api/users/{id}", existingUser.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("firstName", equalTo(updateRequestDto.firstName()))
            .body("lastName", equalTo(updateRequestDto.lastName()))
            .body("email", equalTo(updateRequestDto.email()));
    }

    @Test
    void shouldDeleteUser() {
        var token = TestUtil.authenticate("admin@example.com", "123456");
        var existingUser = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "123456"));

        given()
            .header("Authorization", "Bearer " + token)
            .pathParam("id", existingUser.getId())
        .when()
            .delete("/api/users/{id}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldGetUserById() {
        var token = TestUtil.authenticate("admin@example.com", "123456");
        var existingUser = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "123456"));

        given()
            .header("Authorization", "Bearer " + token)    
            .pathParam("id", existingUser.getId())
        .when()
            .get("/api/users/{id}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("id", equalTo(existingUser.getId().intValue()))
            .body("firstName", equalTo(existingUser.getFirstName()))
            .body("lastName", equalTo(existingUser.getLastName()))
            .body("email", equalTo(existingUser.getEmail()));
    }

    @Test
    void shouldGetAllUsers() {
        var token = TestUtil.authenticate("admin@example.com", "123456");
        var user1 = new User(null, "John", "Doe", "johndoe@example.com", "123456");
        var user2 = new User(null, "Jane", "Smith", "janesmith@example.com", "123456");
        userRepository.saveAll(Arrays.asList(user1, user2));

        given()
            .header("Authorization", "Bearer " + token)    
        .when()
            .get("/api/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("content.size()", equalTo(3))
            .body("content.firstName", hasItems("John", "Jane"))
            .body("content.lastName", hasItems("Doe", "Smith"))
            .body("content.email", hasItems("johndoe@example.com", "janesmith@example.com"));
    }

}