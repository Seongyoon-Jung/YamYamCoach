package com.yamyam.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yamyam.diet.entity.CourseSchedule;
import com.yamyam.diet.repository.CourseScheduleRepository;
import com.yamyam.dto.request.FoodData;

@Service
public class FoodServiceImpl implements FoodService {
	private final CourseScheduleRepository courseScheduleRepository;
	public FoodServiceImpl(CourseScheduleRepository courseScheduleRepository) {
		this.courseScheduleRepository = courseScheduleRepository;
	}

	@Override
	public void saveCourseSchedule(LocalDate date, String course, String menu) {
		
	}

	@Override
	public void saveAllCourseSchedule(List<FoodData> list) {
		List<CourseSchedule> data = new ArrayList<>();
		
		for (FoodData foodData : list) {
			data.add(new CourseSchedule(foodData.getDate(), foodData.getCourse(), foodData.getFood().get(0)));
		}
		
		courseScheduleRepository.saveAll(data);
	}

}
