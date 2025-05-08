package com.yamyam.dto;

import java.time.LocalDate;

import com.yamyam.entity.UserEntity;

public class CurrentUser {
	private int userId;
	private String username;
	private boolean gender;
	private LocalDate birthDate;
	private int height;
	private int weight;
	private int targetWeight;
	private boolean isSurveyed;
	
	public CurrentUser() {
		// TODO Auto-generated constructor stub
	}

	public CurrentUser(int userId, String username, boolean gender, LocalDate birthDate, int height, int weight,
			int targetWeight, boolean isSurveyed) {
		this.userId = userId;
		this.username = username;
		this.gender = gender;
		this.birthDate = birthDate;
		this.height = height;
		this.weight = weight;
		this.targetWeight = targetWeight;
		this.isSurveyed = isSurveyed;
	}

	public static CurrentUser currentUser(UserEntity userEntity) {
		return new CurrentUser(
				userEntity.getUserId(),
				userEntity.getUsername(),
				userEntity.isGender(),
				userEntity.getBirthDate(),
				userEntity.getHeight(),
				userEntity.getWeight(),
				userEntity.getTargetWeight(),
				userEntity.isSurveyed()
				);
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

	public boolean isSurveyed() {
		return isSurveyed;
	}

	public void setSurveyed(boolean isSurveyed) {
		this.isSurveyed = isSurveyed;
	}
	
	
}
