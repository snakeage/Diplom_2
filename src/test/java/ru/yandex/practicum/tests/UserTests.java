package ru.yandex.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.steps.ApiSteps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.notNullValue;

public class UserTests extends BaseTest {

    private final ApiSteps apiSteps = new ApiSteps();
    private String accessToken;
    private String email;

    @Before
    public void setUp() {
        email = RandomStringUtils.randomAlphabetic(15) + System.currentTimeMillis() + "@yandex.ru";
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserTest() {
        accessToken = apiSteps.registerAndGetToken(email, "password123", "TestUser");

        Response response = apiSteps.loginUser(email, "password123");
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalToIgnoringCase(email))
                .body("user.name", equalTo("TestUser"));
    }

    @Test
    @DisplayName("Создание уже существующего пользователя")
    public void createExistingUserTest() {
        accessToken = apiSteps.registerAndGetToken(email, "password123", "TestUser");

        User user = new User(email, "password123", "TestUser");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @After
    public void tearDown() {
        apiSteps.deleteUser(accessToken);
    }
}
