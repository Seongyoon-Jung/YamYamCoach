package com.yamyam.food.dto;

import java.util.List;

public class FoodRequest {
    private List<FoodData> diet;

    public List<FoodData> getDiet() {
        return diet;
    }

    public void setDiet(List<FoodData> diet) {
        this.diet = diet;
    }

}