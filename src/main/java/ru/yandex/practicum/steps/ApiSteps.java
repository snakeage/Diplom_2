package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.model.User;

import static io.restassured.RestAssured.given;

public class ApiSteps {

    @Step("Регистрация пользователя")
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
    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            try {
                Response response = given()
                        .header("Authorization", accessToken)
                        .delete("/api/auth/user");

                response.then()
                        .statusCode(202);
            } catch (Exception e) {
                System.err.println("Не удалось удалить пользователя: " + e.getMessage());
            }
        }
    }
}