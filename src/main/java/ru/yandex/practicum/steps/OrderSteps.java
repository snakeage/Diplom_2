package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.yandex.practicum.model.Ingredient;
import ru.yandex.practicum.model.IngredientResponse;
import ru.yandex.practicum.model.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Получение списка ингредиентов")
    public List<String> getIngredientIds() {
        Response response = given()
                .get("/api/ingredients");

        IngredientResponse ingredientResponse = response.as(IngredientResponse.class);
        List<Ingredient> ingredients = ingredientResponse.getData();
        if (ingredients == null || ingredients.size() < 2) {
            throw new IllegalStateException("Недостаточно ингредиентов или API вернул null: " + response.asString());
        }
        return List.of(
                ingredients.get(0).getId(),
                ingredients.get(1).getId()
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
}