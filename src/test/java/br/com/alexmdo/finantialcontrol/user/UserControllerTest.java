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
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserUpdateRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @MockBean
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
    void shouldCreateUser() {
        var createRequestDto = new UserCreateRequestDto("John", "Doe", "johndoe@example.com");
        var expectedUser = new User(1L, "John", "Doe", "johndoe@example.com");

        when(userService.createUser(any())).thenReturn(expectedUser);

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
    void shouldUpdateUser() {
        var userId = 1L;
        var updateRequest = new UserUpdateRequestDto("John", "Doe", "johndoe@example.com");
        var expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.getUserById(userId)).thenReturn(expectedUser);
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
    void shouldDeleteUser() {
        var userId = 1L;

        given()
            .pathParam("userId", userId)
        .when()
            .delete("/api/users/{userId}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldGetUserById() {
        var userId = 1L;
        var expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.getUserById(userId)).thenReturn(expectedUser);

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
    void shouldGetAllUsers() {
        var expectedUserList = Arrays.asList(
                new User(1L, "John", "Doe", "johndoe@example.com"),
                new User(2L, "Jane", "Smith", "janesmith@example.com"));
        var pages = new PageImpl<>(expectedUserList);

        when(userService.getAllUsers(any())).thenReturn(pages);

        given()
        .when()
            .get("/api/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(pages.getSize()))
            .body("content.id", hasItems(1, 2))
            .body("content.firstName", hasItems("John", "Jane"))
            .body("content.lastName", hasItems("Doe", "Smith"))
            .body("content.email", hasItems("johndoe@example.com", "janesmith@example.com"));
    }
}
