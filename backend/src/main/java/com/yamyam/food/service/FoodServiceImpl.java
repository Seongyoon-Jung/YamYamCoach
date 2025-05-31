package com.yamyam.food.service;

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
import com.yamyam.food.dto.FoodData;
import com.yamyam.food.dto.FoodsResponse;
import com.yamyam.s3.service.S3UploadService;

@Service
public class FoodServiceImpl implements FoodService {
	private final CourseScheduleRepository courseScheduleRepository;
	private final DishRepository dishRepository;
	private final ScheduleDishRepository scheduleDishRepository;
	private final S3UploadService s3UploadService;
	
	public FoodServiceImpl(CourseScheduleRepository courseScheduleRepository, DishRepository dishRepository,
			ScheduleDishRepository scheduleDishRepository, S3UploadService s3UploadService) {
		this.courseScheduleRepository = courseScheduleRepository;
		this.dishRepository = dishRepository;
		this.scheduleDishRepository = scheduleDishRepository;
		this.s3UploadService = s3UploadService;
	}

	@Override
	public List<FoodsResponse> getFoodsDate(LocalDate date) {
		// course_schedule 에서 date에 해당하는 컬럼중 코스 타입에 따른 schedule_id 받기
		List<CourseSchedule> list = courseScheduleRepository.findByScheduleDate(date);
		
		List<FoodsResponse> data = new ArrayList<>();
		
		
		//schedule_dish에서 couresSchedule.schedule_id 에 해당하는 dish_id 찾기
		for (CourseSchedule courseSchedule : list) {
			FoodsResponse foodsResponse = new FoodsResponse();
			foodsResponse.setDate(date);
			foodsResponse.setCourse(courseSchedule.getCourseType());
			foodsResponse.setImgUrl(courseSchedule.getImgUrl());
			List<ScheduleDish> dishs = scheduleDishRepository.findByIdScheduleIdOrderByServingOrder(courseSchedule.getScheduleId());
			List<String> foods = new ArrayList<>();
			for (ScheduleDish scheduleDish : dishs) {
				foods.add(dishRepository.findById(scheduleDish.getId().getDishId()).get().getDishName());
			}
			
			foodsResponse.setFood(foods);
			
			data.add(foodsResponse);
		}
		return data;
	}
	
	
	
	
	@Override
	public void saveAllCourseSchedule(List<FoodData> list) {
		List<CourseSchedule> data = new ArrayList<>();
		
		for (FoodData foodData : list) {
			if(!courseScheduleRepository.existsByScheduleDateAndCourseType(foodData.getDate(), foodData.getCourse())) {
				data.add(new CourseSchedule(foodData.getDate(), foodData.getCourse(), foodData.getFood().get(0), foodData.getImgUrl()));
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
				if(!scheduleDishRepository.existsByDish_DishIdAndCourseSchedule_ScheduleId(dish.get().getDishId(),courseSchedule.get().getScheduleId()) && !menu.equals("")) {
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

	@Override
	public void modifyAllCourseSchedule(List<FoodData> list) {
		List<CourseSchedule> data = new ArrayList<>();
		
		for (FoodData foodData : list) {
			Optional<CourseSchedule> courseSchedule = courseScheduleRepository.findByScheduleDateAndCourseType(foodData.getDate(), foodData.getCourse());
			courseSchedule.get().setCourseName(foodData.getFood().get(0));
			
			//사진이 수정됨 수정 전 이미지 삭제후 다시 url 주소 넣기
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println(courseSchedule.get().getImgUrl()+" "+foodData.getImgUrl());
			if(courseSchedule.get().getImgUrl()!=null && !courseSchedule.get().getImgUrl().equals(foodData.getImgUrl())) {
				s3UploadService.deleteImage(courseSchedule.get().getImgUrl());
				courseSchedule.get().setImgUrl(foodData.getImgUrl());
			}
			
			data.add(courseSchedule.get());
		}
		
		courseScheduleRepository.saveAll(data);
		
	}

	@Override
	public void modifyAllScheduleDish(List<FoodData> list) {
		List<ScheduleDish> data = new ArrayList<>();
		
		for (FoodData foodData : list) {
			Optional<CourseSchedule> courseSchedule = courseScheduleRepository.findByScheduleDateAndCourseType(foodData.getDate(), foodData.getCourse());
			
			int order = 1;
			for (String menu : foodData.getFood()) {
				Optional<Dish> dish = dishRepository.findByDishName(menu);
				ScheduleDish scheduleDish = scheduleDishRepository.findByCourseSchedule_ScheduleIdAndServingOrder(courseSchedule.get().getScheduleId(),order);
				
				//음식명이 "" 상태로 들어온다면
				if(menu.equals("")) {
					 scheduleDishRepository.delete(scheduleDish);
				}
				
				// 음식명을 수정했다면
				else if (scheduleDish != null && scheduleDish.getDish().getDishId() != dish.get().getDishId()) {
				    scheduleDishRepository.delete(scheduleDish);
					ScheduleDishId id = new ScheduleDishId(dish.get().getDishId(), courseSchedule.get().getScheduleId());
				    ScheduleDish s = new ScheduleDish();
				    s.setId(id);
				    s.setDish(dish.get());
				    s.setCourseSchedule(courseSchedule.get());
				    s.setServingOrder(order);
				    data.add(s); // ← 버그: 여기도 scheduleDish가 아닌 새로 만든 s를 넣어야 함
				} 
				order++;
			}	
		}	
		
		scheduleDishRepository.saveAll(data);
		
	}


}
