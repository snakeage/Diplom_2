package ru.yandex.practicum.tests;

import org.junit.Before;
import ru.yandex.practicum.config.RestConfig;

public class BaseTest {
    @Before
    public void setUp() {
        RestConfig.setUp();
    }
}