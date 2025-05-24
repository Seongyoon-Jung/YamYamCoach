package com.yamyam.dto.response;

import java.time.LocalDate;
import java.util.List;

public class FoodsResponse {
	private LocalDate date;
	private String course;
	private List<String> food;
	private String imgUrl;
	
	public FoodsResponse() {
		// TODO Auto-generated constructor stub
	}

	public FoodsResponse(LocalDate date, String course, List<String> food, String imgUrl) {
		this.date = date;
		this.course = course;
		this.food = food;
		this.imgUrl = imgUrl;
	}

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	
	
}
