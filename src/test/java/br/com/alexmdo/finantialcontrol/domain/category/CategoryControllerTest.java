package br.com.alexmdo.finantialcontrol.domain.category;

import br.com.alexmdo.finantialcontrol.domain.category.dto.CategoryCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.category.dto.CategoryUpdateRequestDto;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        this.user = createNewUser();

        RestAssured.port = port;
    }

    @Test
    public void testCreateCategory() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        CategoryCreateRequestDto createRequestDto = new CategoryCreateRequestDto(
                "Category",
                "Blue",
                "piggy-bank",
                Category.Type.EXPENSE
        );

        // Perform POST request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(createRequestDto)
        .when()
            .post("/api/users/me/categories")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo("Category"))
            .body("color", equalTo("Blue"))
            .body("icon", equalTo("piggy-bank"))
            .body("type", equalTo("EXPENSE"));
    }

    @Test
    public void testUpdateCategory() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");

        CategoryUpdateRequestDto updateRequestDto = new CategoryUpdateRequestDto(
                "Updated Category",
                "Green",
                "piggy-bank",
                Category.Type.INCOME
        );
        var category = createNewCategory();


        // Perform PUT request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(updateRequestDto)
        .when()
            .put("/api/users/me/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo("Updated Category"))
            .body("color", equalTo("Green"))
            .body("icon", equalTo("piggy-bank"))
            .body("type", equalTo("INCOME"));
    }

    @Test
    public void testDeleteCategory() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var category = createNewCategory();

        // Perform DELETE request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/api/users/me/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testGetCategories() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var category = createNewCategory();

        // Perform GET request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/categories")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(1))
            .body("content[0].id", equalTo(category.getId().intValue()))
            .body("content[0].name", equalTo(category.getName()))
            .body("content[0].color", equalTo(category.getColor()))
            .body("content[0].icon", equalTo(category.getIcon()))
            .body("content[0].type", equalTo(category.getType().toString()));
    }

    @Test
    public void testGetCategoryById() {
        // Prepare test data
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var category = createNewCategory();

        // Perform GET request
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(category.getId().intValue()))
            .body("name", equalTo(category.getName()))
            .body("color", equalTo(category.getColor()))
            .body("icon", equalTo(category.getIcon()))
            .body("type", equalTo(category.getType().toString()));
    }

    private User createNewUser() {
        return userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "$2a$10$m9FiHBdOWEgZpnzylyc8ZOHSN5Lbt9qwG7lIJxpeq4KRJwa1oF/Tq"));
    }

    private Category createNewCategory() {
        return categoryRepository.save(new Category(
                null,
                "Category",
                "Blue",
                "piggy-bank",
                Category.Type.EXPENSE,
                new User(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword())
        ));
    }
}
