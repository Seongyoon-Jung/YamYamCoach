package com.yamyam.service;

import java.time.LocalDate;
import java.util.List;

import com.yamyam.dto.request.FoodData;
import com.yamyam.dto.response.FoodsResponse;

public interface FoodService {	
	public void saveAllCourseSchedule(List<FoodData> list);
	
	public void saveAllDish(List<FoodData> list);
	
	public void saveAllScheduleDish(List<FoodData> list);
	
	public List<FoodsResponse> getFoodsDate(LocalDate date);
}
