package ru.yandex.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.steps.ApiSteps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserTests extends BaseTest {

    private final ApiSteps apiSteps = new ApiSteps();
    private String accessToken;

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserTest() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        User user = new User(email, "password123", "TestUser");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo("TestUser"));

        // Очистка
        accessToken = response.then().extract().path("accessToken");
        apiSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createExistingUserTest() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String password = "password123";
        String name = "TestUser";

        // Создаем
        accessToken = apiSteps.registerAndGetToken(email, password, name);

        // Повторно
        User user = new User(email, password, name);
        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));

        // Очистка
        apiSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля")
    public void createUserWithoutRequiredFieldTest() {
        User user = new User("", "password123", "TestUser");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}