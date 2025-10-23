package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.steps.ApiSteps;

import static org.hamcrest.Matchers.*;

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
    @Description("Проверка успешного создания уникального пользователя и логина")
    public void createUniqueUserTest() {
        Response response = apiSteps.registerUser(email, "password123", "TestUser");
        accessToken = response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .extract().path("accessToken");

        Response loginResponse = apiSteps.loginUser(email, "password123");
        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalToIgnoringCase(email))
                .body("user.name", equalTo("TestUser"));
    }

    @Test
    @DisplayName("Создание уже существующего пользователя")
    @Description("Проверка ошибки при попытке создать уже зарегистрированного пользователя")
    public void createExistingUserTest() {
        // первая регистрация
        Response first = apiSteps.registerUser(email, "password123", "TestUser");
        accessToken = first.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .extract().path("accessToken");

        // вторая регистрация тем же пользователем
        Response secondResponse = apiSteps.registerUserRaw(email, "password123", "TestUser");
        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка ошибки при создании пользователя без email")
    public void createUserWithoutEmailTest() {
        Response response = apiSteps.registerUserRaw(null, "password123", "TestUser");
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка ошибки при создании пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        Response response = apiSteps.registerUserRaw(email, null, "TestUser");
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка ошибки при создании пользователя без имени")
    public void createUserWithoutNameTest() {
        Response response = apiSteps.registerUserRaw(email, "password123", null);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Response response = apiSteps.deleteUser(accessToken);
            if (response != null) {
                response.then().statusCode(202);
            }
        }
    }
}