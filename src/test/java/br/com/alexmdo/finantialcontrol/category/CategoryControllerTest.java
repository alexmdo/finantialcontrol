package br.com.alexmdo.finantialcontrol.category;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import br.com.alexmdo.finantialcontrol.category.dto.CategoryCreateRequestDto;
import br.com.alexmdo.finantialcontrol.category.dto.CategoryUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.user.User;
import br.com.alexmdo.finantialcontrol.user.UserRepository;
import io.restassured.RestAssured;
    
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        RestAssured.port = port;
    }

    @Test
    void testCreateCategory() {
        var newUser = userRepository.save(new User(null, "John", "Doe", "johndoe1@email.com"));
        
        var createRequestDto = new CategoryCreateRequestDto(
                "Food", "red", "utensils", Category.Type.EXPENSE, newUser.getId());

        given()
            .contentType("application/json")
            .body(createRequestDto)
        .when()
            .post("/api/categories")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo(createRequestDto.name()))
            .body("color", equalTo(createRequestDto.color()))
            .body("icon", equalTo(createRequestDto.icon()))
            .body("type", equalTo(createRequestDto.type().name()));
    }

    @Test
    void testUpdateCategory() {
        // Create a category for update
        var newUser = userRepository.save(new User(null, "John", "Doe", "johndoe2@email.com"));
        var category = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, newUser);
        category = categoryRepository.save(category);

        var updateRequestDto = new CategoryUpdateRequestDto(
                "Groceries", "green", "shopping-cart", Category.Type.EXPENSE);

        given()
            .contentType("application/json")
            .body(updateRequestDto)
        .when()
            .put("/api/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(updateRequestDto.name()))
            .body("color", equalTo(updateRequestDto.color()))
            .body("icon", equalTo(updateRequestDto.icon()))
            .body("type", equalTo(updateRequestDto.type().name()));
    }

    @Test
    void testDeleteCategory() {
        // Create a category for deletion
        var newUser = userRepository.save(new User(null, "John", "Doe", "johndoe3@email.com"));
        var category = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, newUser);
        category = categoryRepository.save(category);

        given()
        .when()
            .delete("/api/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void testGetCategories() {
        // Create some categories for testing
        var newUser = userRepository.save(new User(null, "John", "Doe", "johndoe4@email.com"));
        var category1 = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, newUser);
        var category2 = new Category(null, "Salary", "green", "money-bill", Category.Type.INCOME, newUser);
        category1 = categoryRepository.save(category1);
        category2 = categoryRepository.save(category2);

        given()
        .when()
            .get("/api/categories")
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
        // Create a category for retrieval
        var newUser = userRepository.save(new User(null, "John", "Doe", "johndoe5@email.com"));
        var category = new Category(null, "Food", "red", "utensils", Category.Type.EXPENSE, newUser);
        category = categoryRepository.save(category);

        given()
        .when()
            .get("/api/categories/{id}", category.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(category.getName()))
            .body("color", equalTo(category.getColor()))
            .body("icon", equalTo(category.getIcon()))
            .body("type", equalTo(category.getType().name()));
    }
}
    