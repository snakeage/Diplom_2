package ru.yandex.practicum.model;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private List<String> ingredients;

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}