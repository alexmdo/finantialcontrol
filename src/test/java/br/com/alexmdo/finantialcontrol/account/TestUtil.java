package br.com.alexmdo.finantialcontrol.account;

import static io.restassured.RestAssured.given;

import br.com.alexmdo.finantialcontrol.auth.dto.TokenJWTDto;
import io.restassured.http.ContentType;

public class TestUtil {

    public static String authenticate(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"login\":\"" + username + "\",\"password\":\"" + password + "\"}")
            .when()
                .post("/api/auth")
            .then()
                .extract()
                .as(TokenJWTDto.class)
                .token();
    }

}
