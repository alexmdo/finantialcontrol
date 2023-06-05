package br.com.alexmdo.finantialcontrol.category;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import br.com.alexmdo.finantialcontrol.category.dto.CategoryCreateRequestDto;
import br.com.alexmdo.finantialcontrol.category.dto.CategoryUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.user.User;
import br.com.alexmdo.finantialcontrol.user.UserRepository;
import br.com.alexmdo.finantialcontrol.util.TestUtil;
import io.restassured.RestAssured;
    
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(new User(null, "John", "Doe", "johndoe@example.com", "$2a$10$m9FiHBdOWEgZpnzylyc8ZOHSN5Lbt9qwG7lIJxpeq4KRJwa1oF/Tq"));
        RestAssured.port = port;
    }

    @Test
    void testCreateCategory() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        var createRequestDto = new CategoryCreateRequestDto(
                "Food", "red", "utensils", Category.Type.EXPENSE, user.getId());

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .body(createRequestDto)
        .when()
            .post("/api/users/me/categories")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo(createRequestDto.name()))
            .body("color", equalTo(createRequestDto.color()))
            .body("icon", equalTo(createRequestDto.icon()))
            .body("type", equalTo(createRequestDto.type().name()));
    }

    @Test
    void testUpdateCategory() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        // Create a category for update
        var category = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, user);
        category = categoryRepository.save(category);

        var updateRequestDto = new CategoryUpdateRequestDto(
                "Groceries", "green", "shopping-cart", Category.Type.EXPENSE);

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .body(updateRequestDto)
        .when()
            .put("/api/users/me/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(updateRequestDto.name()))
            .body("color", equalTo(updateRequestDto.color()))
            .body("icon", equalTo(updateRequestDto.icon()))
            .body("type", equalTo(updateRequestDto.type().name()));
    }

    @Test
    void testDeleteCategory() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        // Create a category for deletion
        var category = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, user);
        category = categoryRepository.save(category);

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/api/users/me/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void testGetCategories() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        // Create some categories for testing
        var category1 = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, user);
        var category2 = new Category(null, "Salary", "green", "money-bill", Category.Type.INCOME, user);
        category1 = categoryRepository.save(category1);
        category2 = categoryRepository.save(category2);

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/categories")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(2))
            .body("content[0].name", equalTo(category1.getName()))
            .body("content[0].color", equalTo(category1.getColor()))
            .body("content[0].icon", equalTo(category1.getIcon()))
            .body("content[0].type", equalTo(category1.getType().name()))
            .body("content[1].name", equalTo(category2.getName()))
            .body("content[1].color", equalTo(category2.getColor()))
            .body("content[1].icon", equalTo(category2.getIcon()))
            .body("content[1].type", equalTo(category2.getType().name()));
    }

    @Test
    void testGetCategoryById() {
        var token = TestUtil.authenticate("johndoe@example.com", "123456");
        // Create a category for retrieval
        var category = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, user);
        category = categoryRepository.save(category);

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(category.getName()))
            .body("color", equalTo(category.getColor()))
            .body("icon", equalTo(category.getIcon()))
            .body("type", equalTo(category.getType().name()));
    }
}
    