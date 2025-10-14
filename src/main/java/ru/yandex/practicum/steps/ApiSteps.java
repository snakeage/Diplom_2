package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.yandex.practicum.model.Ingredient;
import ru.yandex.practicum.model.IngredientResponse;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.User;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiSteps {

    @Step("Регистрация пользователя и получение токена")
    public String registerAndGetToken(String email, String password, String name) {
        User user = new User(email, password, name);
        RequestSpecification request = given()
                .header("Content-Type", "application/json")
                .body(user);

        Response response = request.post("/api/auth/register");

        try {
            return response.then()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .extract().path("accessToken");
        } catch (AssertionError e) {
            System.err.println("Registration failed: " + response.asString());
            throw e;
        }
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
        Response response = given()
                .get("/api/ingredients");

        IngredientResponse ingredientResponse = response.as(IngredientResponse.class);
        List<Ingredient> ingredients = ingredientResponse.getData();
        if (ingredients == null || ingredients.size() < 2) {
            throw new IllegalStateException("Insufficient or null ingredients returned from API: " + response.asString());
        }
        // Возвращаем два id ингредиентов
        return List.of(
                ingredients.get(0).get_id(),
                ingredients.get(1).get_id()
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
        RequestSpecification request = given()
                .header("Content-Type", "application/json")
                .body(order);
        return request.post("/api/orders");
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
                System.err.println("Delete user failed: " + e.getMessage());
            }
        }
    }
}
