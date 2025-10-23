package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.model.User;

import static io.restassured.RestAssured.given;

/**
 * Шаги для работы с API.
 * Методы возвращают только Response – проверки делаются в тестах.
 */
public class ApiSteps {

    @Step("Регистрация пользователя (универсальный метод)")
    public Response registerUser(String email, String password, String name) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");
    }

    /**
     * «Сырой» запрос регистрации – без создания объекта User.
     * Используется в тестах, где нужно отправить запрос вручную.
     */
    @Step("Сырой запрос регистрации пользователя")
    public Response registerUserRaw(String email, String password, String name) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        if (accessToken != null) {
            return given()
                    .header("Authorization", accessToken)
                    .delete("/api/auth/user");
        }
        return null;
    }
}