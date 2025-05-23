package com.yamyam.dto.request;

import java.util.List;

public class FoodRequest {
    private List<FoodData> diet;
    private String imgUrl;

    public List<FoodData> getDiet() {
        return diet;
    }

    public void setDiet(List<FoodData> diet) {
        this.diet = diet;
    }

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}