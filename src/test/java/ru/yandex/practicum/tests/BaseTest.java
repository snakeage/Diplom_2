package ru.yandex.practicum.tests;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class BaseTest {

    @BeforeClass
    public static void setUpAllureAndBaseURI() {
        // Для Allure
        System.setProperty("allure.results.directory", "target/allure-results");

        // Базовый URL для всех API-запросов
        RestAssured.baseURI = "https://stellarburgers.education-services.ru"; // твой рабочий стенд
    }
}
