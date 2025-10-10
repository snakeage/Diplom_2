package ru.yandex.practicum.config;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

public class RestConfig {
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}