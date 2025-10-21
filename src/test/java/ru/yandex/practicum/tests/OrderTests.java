package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.steps.ApiSteps;
import ru.yandex.practicum.steps.OrderSteps;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderTests extends BaseTest {

    private final ApiSteps apiSteps = new ApiSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private String accessToken;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        String email = RandomStringUtils.randomAlphabetic(15) + System.currentTimeMillis() + "@yandex.ru";
        Response response = apiSteps.registerUser(email, "password123", "TestUser");
        accessToken = response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .extract().path("accessToken");
        validIngredients = orderSteps.getIngredientIds();
        if (validIngredients == null || validIngredients.isEmpty()) {
            throw new IllegalStateException("Не удалось получить ингредиенты из API");
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка успешного создания заказа с авторизацией")
    public void createOrderWithAuthTest() {
        Order order = new Order(validIngredients);
        Response response = orderSteps.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка ошибки при создании заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        // ВНИМАНИЕ: Согласно документации API, эндпоинт /api/orders должен возвращать 401 Unauthorized
        // с сообщением "You should be authorised" для неавторизованных запросов.
        // Однако текущая реализация API возвращает 200 OK (проверено через Postman: https://stellarburgers.education-services.ru/api/orders).
        // Тест оставлен с ожиданием 401, как указано в документации.
        // Необходимо уточнить у команды API, является ли это багом или изменением в спецификации.
        Order order = new Order(validIngredients);
        Response response = orderSteps.createOrderWithoutAuth(order);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при создании заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        Response response = orderSteps.createOrder(order, accessToken);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    @Description("Проверка ошибки при создании заказа с неверным хешем ингредиента")
    public void createOrderWithInvalidIngredientTest() {
        Order order = new Order(List.of("invalid_hash_123"));
        Response response = orderSteps.createOrder(order, accessToken);

        response.then()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            try {
                apiSteps.deleteUser(accessToken);
            } catch (Exception e) {
                System.err.println("Не удалось удалить пользователя в tearDown: " + e.getMessage());
            }
        }
    }
}