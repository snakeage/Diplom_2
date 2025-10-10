package ru.yandex.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.steps.ApiSteps;

import static org.hamcrest.Matchers.equalTo;

public class LoginTests extends BaseTest {

    private final ApiSteps apiSteps = new ApiSteps();
    private String accessToken;

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void loginExistingUserTest() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String password = "password123";
        String name = "TestUser";

        // Регистрация
        accessToken = apiSteps.registerAndGetToken(email, password, name);

        // Логин
        Response response = apiSteps.loginUser(email, password);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем")
    public void loginWithWrongCredentialsTest() {
        Response response = apiSteps.loginUser("wrong@yandex.ru", "wrongpassword");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        apiSteps.deleteUser(accessToken);
    }
}