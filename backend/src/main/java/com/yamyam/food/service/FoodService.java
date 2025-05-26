package com.yamyam.food.service;

import java.time.LocalDate;
import java.util.List;

import com.yamyam.food.dto.FoodData;
import com.yamyam.food.dto.FoodsResponse;

public interface FoodService {	
	public void saveAllCourseSchedule(List<FoodData> list);
	
	public void saveAllDish(List<FoodData> list);
	
	public void saveAllScheduleDish(List<FoodData> list);
	
	public List<FoodsResponse> getFoodsDate(LocalDate date);
	
	public void modifyAllCourseSchedule(List<FoodData> list);
	
	public void modifyAllScheduleDish(List<FoodData> list);
}
