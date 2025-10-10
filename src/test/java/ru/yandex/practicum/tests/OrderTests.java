package ru.yandex.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.steps.ApiSteps;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class OrderTests extends BaseTest {

    private final ApiSteps apiSteps = new ApiSteps();
    private String accessToken;
    private List<String> validIngredients;

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthTest() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        accessToken = apiSteps.registerAndGetToken(email, "password123", "TestUser");
        validIngredients = apiSteps.getIngredientIds();

        Order order = new Order(validIngredients);
        Response response = apiSteps.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        validIngredients = apiSteps.getIngredientIds();
        Order order = new Order(validIngredients);

        Response response = apiSteps.createOrderWithoutAuth(order);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        accessToken = apiSteps.registerAndGetToken(email, "password123", "TestUser");
        validIngredients = apiSteps.getIngredientIds();

        Order order = new Order(validIngredients);
        Response response = apiSteps.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        accessToken = apiSteps.registerAndGetToken(email, "password123", "TestUser");

        Order order = new Order(Collections.emptyList());
        Response response = apiSteps.createOrder(order, accessToken);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsTest() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        accessToken = apiSteps.registerAndGetToken(email, "password123", "TestUser");

        Order order = new Order(List.of("invalid_hash_123"));
        Response response = apiSteps.createOrder(order, accessToken);

        response.then()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        apiSteps.deleteUser(accessToken);
    }
}