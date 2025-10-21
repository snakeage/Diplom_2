package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.steps.ApiSteps;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTests extends BaseTest {

    private final ApiSteps apiSteps = new ApiSteps();
    private String accessToken;
    private String email;

    @Before
    public void setUp() {
        email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        Response response = apiSteps.registerUser(email, "password123", "TestUser");
        accessToken = response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин существующего пользователя")
    @Description("Проверка успешного логина существующего пользователя")
    public void loginExistingUserTest() {
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
    @DisplayName("Логин с неверным email")
    @Description("Проверка ошибки при попытке логина с неверным email")
    public void loginWithWrongEmailTest() {
        Response response = apiSteps.loginUser("wrong" + email, "password123");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка ошибки при попытке логина с неверным паролем")
    public void loginWithWrongPasswordTest() {
        Response response = apiSteps.loginUser(email, "wrongpassword");

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