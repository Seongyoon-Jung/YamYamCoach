package com.yamyam.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.yamyam.diet.entity.CourseSchedule;
import com.yamyam.diet.entity.Dish;
import com.yamyam.diet.entity.ScheduleDish;
import com.yamyam.diet.entity.ScheduleDishId;
import com.yamyam.diet.repository.CourseScheduleRepository;
import com.yamyam.diet.repository.DishRepository;
import com.yamyam.diet.repository.ScheduleDishRepository;
import com.yamyam.dto.request.FoodData;

@Service
public class FoodServiceImpl implements FoodService {
	private final CourseScheduleRepository courseScheduleRepository;
	private final DishRepository dishRepository;
	private final ScheduleDishRepository scheduleDishRepository;
	public FoodServiceImpl(CourseScheduleRepository courseScheduleRepository, DishRepository dishRepository,
			ScheduleDishRepository scheduleDishRepository) {
		this.courseScheduleRepository = courseScheduleRepository;
		this.dishRepository = dishRepository;
		this.scheduleDishRepository = scheduleDishRepository;
	}


	@Override
	public void saveAllCourseSchedule(List<FoodData> list) {
		List<CourseSchedule> data = new ArrayList<>();
		
		for (FoodData foodData : list) {
			if(!courseScheduleRepository.existsByScheduleDateAndCourseType(foodData.getDate(), foodData.getCourse())) {
				data.add(new CourseSchedule(foodData.getDate(), foodData.getCourse(), foodData.getFood().get(0)));
			}
		}
		
		courseScheduleRepository.saveAll(data);
	}


	// dish 테이블에 음식 정보를 올림
	@Override
	public void saveAllDish(List<FoodData> list) {
		List<Dish> data = new ArrayList<>();
		List<String> menus = new ArrayList<>();
		
		for (FoodData foodData : list) {
			for (String menu : foodData.getFood()) {
				if(!dishRepository.existsByDishName(menu) && !menus.contains(menu)) {
					menus.add(menu);
					Dish dish = new Dish();
					dish.setDishName(menu);
					data.add(dish);
				}				
			}
			
		}
		
		dishRepository.saveAll(data);
	}


	// schedule_dish 에 insert
	@Override
	public void saveAllScheduleDish(List<FoodData> list) {
		//dish Entity, CourseSchedule Entity
		List<ScheduleDish> data = new ArrayList<>();
		
		
		for (FoodData foodData : list) {
			Optional<CourseSchedule> courseSchedule = courseScheduleRepository.findByScheduleDateAndCourseType(foodData.getDate(), foodData.getCourse());
			int order = 1;
			for (String menu : foodData.getFood()) {
				Optional<Dish> dish = dishRepository.findByDishName(menu);
				System.out.println("menu : " + menu);
				if(!scheduleDishRepository.existsByDish_DishIdAndCourseSchedule_ScheduleId(dish.get().getDishId(),courseSchedule.get().getScheduleId())) {
					ScheduleDishId id = new ScheduleDishId(dish.get().getDishId(), courseSchedule.get().getScheduleId());

					ScheduleDish scheduleDish = new ScheduleDish();
					scheduleDish.setId(id);
					scheduleDish.setDish(dish.get());
					scheduleDish.setCourseSchedule(courseSchedule.get());
					scheduleDish.setServingOrder(order);
					data.add(scheduleDish);
				}
				
				order++;
			}
			
		}
		
		scheduleDishRepository.saveAll(data);
	}

}
