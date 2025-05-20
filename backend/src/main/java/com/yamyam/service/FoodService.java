package com.yamyam.service;

import java.time.LocalDate;
import java.util.List;

import com.yamyam.dto.request.FoodData;

public interface FoodService {

	public void saveCourseSchedule(LocalDate date, String course, String menu);
	
	public void saveAllCourseSchedule(List<FoodData> list);
}
