package com.yamyam.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.dto.request.FoodData;
import com.yamyam.dto.request.FoodRequest;
import com.yamyam.service.FoodService;

@RestController
@RequestMapping("/api/foods")
public class FoodsController {
	private final FoodService foodService;
	public FoodsController(FoodService foodService) {
		this.foodService = foodService;
	}


	@PostMapping("")
	public ResponseEntity<String> saveFoods(@RequestBody FoodRequest request) {
	    for (FoodData item : request.getDiet()) {
	        System.out.println("날짜: " + item.getDate());
	        System.out.println("코스: " + item.getCourse());
	        System.out.println("음식: " + item.getFood());
	        System.out.println();
	    }
	    
	    foodService.saveAllCourseSchedule(request.getDiet());
	    
	    
	    return ResponseEntity.ok("OK");
	}
}
