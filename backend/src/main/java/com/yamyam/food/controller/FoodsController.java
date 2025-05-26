package com.yamyam.food.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.food.dto.FoodData;
import com.yamyam.food.dto.FoodRequest;
import com.yamyam.food.dto.FoodsResponse;
import com.yamyam.food.service.FoodService;

@RestController
@RequestMapping("/api/foods")
public class FoodsController {
	private final FoodService foodService;
	public FoodsController(FoodService foodService) {
		this.foodService = foodService;
	}

	@GetMapping("")
	public ResponseEntity<List<FoodsResponse>> getFoods(@RequestParam("date") LocalDate date){
		List<FoodsResponse>	data = foodService.getFoodsDate(date);
		
		return ResponseEntity.ok(data);
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
	    foodService.saveAllDish(request.getDiet());
	    foodService.saveAllScheduleDish(request.getDiet());
	    
	    
	    return ResponseEntity.ok("OK");
	}
	
	@PutMapping("")
	public ResponseEntity<String> modfiyFoods(@RequestBody FoodRequest request) {
	    for (FoodData item : request.getDiet()) {
	        System.out.println("날짜: " + item.getDate());
	        System.out.println("코스: " + item.getCourse());
	        System.out.println("음식: " + item.getFood());
	        System.out.println();
	    }
	    
	    foodService.modifyAllCourseSchedule(request.getDiet());
	    foodService.saveAllDish(request.getDiet());
	    foodService.modifyAllScheduleDish(request.getDiet());
	    
	    
	    return ResponseEntity.ok("OK");
	}
	
}
