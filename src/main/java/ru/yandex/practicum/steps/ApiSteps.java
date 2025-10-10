package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.model.IngredientResponse;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.User;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiSteps {

    @Step("Регистрация пользователя и получение токена")
    public String registerAndGetToken(String email, String password, String name) {
        User user = new User(email, password, name);
        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");
        return response.then().extract().path("accessToken");
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        User user = new User(email, password, null);
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/login");
    }

    @Step("Получение списка ингредиентов")
    public List<String> getIngredientIds() {
        IngredientResponse ingredientResponse = given()
                .get("/api/ingredients")
                .as(IngredientResponse.class);
        return List.of(
                ingredientResponse.getData().get(0).get_id(),
                ingredientResponse.getData().get(1).get_id()
        );
    }

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-Type", "application/json")
                .body(order)
                .post("/api/orders");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        }
    }
}