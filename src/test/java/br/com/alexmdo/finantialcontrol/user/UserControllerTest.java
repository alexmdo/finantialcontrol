package br.com.alexmdo.finantialcontrol.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserUpdateRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RestAssured.port = port;
    }

    @Test
    void testCreateUser() {
        var createRequestDto = new UserCreateRequestDto("John", "Doe", "johndoe@example.com");
        var expectedUser = new User(1L, "John", "Doe", "johndoe@example.com");

        given()
            .contentType(ContentType.JSON)
            .body(createRequestDto)
        .when()
            .post("/api/users")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", equalTo(expectedUser.getId().intValue()))
            .body("firstName", equalTo(expectedUser.getFirstName()))
            .body("lastName", equalTo(expectedUser.getLastName()))
            .body("email", equalTo(expectedUser.getEmail()));
    }

    @Test
    void testUpdateUser() {
        var userId = 1L;
        var updateRequest = new UserUpdateRequestDto("John", "Doe", "johndoe@example.com");
        var expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.updateUser(any())).thenReturn(expectedUser);

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/api/users/{userId}", userId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(expectedUser.getId().intValue()))
            .body("firstName", equalTo(expectedUser.getFirstName()))
            .body("lastName", equalTo(expectedUser.getLastName()))
            .body("email", equalTo(expectedUser.getEmail()));
    }

    @Test
    void testDeleteUser() {
        var userId = 1L;

        given()
            .pathParam("userId", userId)
        .when()
            .delete("/api/users/{userId}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void testGetUserById() {
        var userId = 1L;
        var expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        given()
            .pathParam("userId", userId)
        .when()
            .get("/api/users/{userId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(expectedUser.getId().intValue()))
            .body("firstName", equalTo(expectedUser.getFirstName()))
            .body("lastName", equalTo(expectedUser.getLastName()))
            .body("email", equalTo(expectedUser.getEmail()));
    }

    @Test
    void testGetAllUsers() {
        var expectedUserList = Arrays.asList(
                new User(1L, "John", "Doe", "johndoe@example.com"),
                new User(2L, "Jane", "Smith", "janesmith@example.com"));

        given()
        .when()
            .get("/api/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(expectedUserList.size()))
            .body("id", hasItems(1, 2))
            .body("firstName", hasItems("John", "Jane"))
            .body("lastName", hasItems("Doe", "Smith"))
            .body("email", hasItems("johndoe@example.com", "janesmith@example.com"));
    }
}
