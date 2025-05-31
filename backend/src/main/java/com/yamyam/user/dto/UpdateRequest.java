package com.yamyam.user.dto;

import java.time.LocalDate;

public class UpdateRequest {
	private int userId;
	private String username;
	private boolean gender;
    private LocalDate birthDate;
    private int height;
    private int weight;
    private int targetWeight;
    
    public UpdateRequest() {
	}

	public UpdateRequest(int userId, String username, boolean gender, LocalDate birthDate, int height, int weight,
			int targetWeight) {
		this.userId = userId;
		this.username = username;
		this.gender = gender;
		this.birthDate = birthDate;
		this.height = height;
		this.weight = weight;
		this.targetWeight = targetWeight;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getTargetWeight() {
		return targetWeight;
	}

	public void setTargetWeight(int targetWeight) {
		this.targetWeight = targetWeight;
	}

	@Override
	public String toString() {
		return "UpdateRequest [userId=" + userId + ", username=" + username + ", gender=" + gender + ", birthDate="
				+ birthDate + ", height=" + height + ", weight=" + weight + ", targetWeight=" + targetWeight + "]";
	}

	
	
}
