package com.yamyam.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FoodData {
	
	@JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
	
    private String course;
    private List<String> food;
    
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public List<String> getFood() {
		return food;
	}
	public void setFood(List<String> food) {
		this.food = food;
	}

    
}