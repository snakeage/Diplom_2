package ru.yandex.practicum.model;

import lombok.Data;

import java.util.List;

@Data
public class IngredientResponse {
    private boolean success;
    private List<Ingredient> data;
}