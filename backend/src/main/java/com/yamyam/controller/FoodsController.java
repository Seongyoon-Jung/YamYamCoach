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

@RestController
@RequestMapping("/api/foods")
public class FoodsController {

	@PostMapping("")
	public ResponseEntity<String> saveFoods(@RequestBody FoodRequest request) {
	    for (FoodData item : request.getDiet()) {
	        System.out.println("날짜: " + item.getDate());
	        System.out.println("코스: " + item.getCourse());
	        System.out.println("음식: " + item.getFood());
	    }

	    return ResponseEntity.ok("OK");
	}
}
