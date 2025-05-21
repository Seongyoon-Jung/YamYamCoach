package com.yamyam.service;

import java.time.LocalDate;
import java.util.List;

import com.yamyam.dto.request.FoodData;

public interface FoodService {	
	public void saveAllCourseSchedule(List<FoodData> list);
	
	public void saveAllDish(List<FoodData> list);
	
	public void saveAllScheduleDish(List<FoodData> list);
}
