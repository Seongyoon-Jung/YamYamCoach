package com.yamyam.dto.response;

import java.time.LocalDate;

import com.yamyam.entity.UserEntity;

public class UserDetailResponse {
	private int userId;
	private String email;
	private String username;
	private boolean gender;
	private LocalDate birthDate;
	// 밑의 값들은 not null이 아니므로 null값이 들어올수 잇으므로 Integer 참조형으로 선언해야함
	private Integer height;
	private Integer weight;
	private Integer targetWeight;

	
	public UserDetailResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public UserDetailResponse(int userId, String email, String username, boolean gender, LocalDate birthDate,
			Integer height, Integer weight, Integer targetWeight) {
		this.userId = userId;
		this.email = email;
		this.username = username;
		this.gender = gender;
		this.birthDate = birthDate;
		this.height = height;
		this.weight = weight;
		this.targetWeight = targetWeight;
	}

	public static UserDetailResponse currentUser(UserEntity userEntity) {
		return new UserDetailResponse(
				userEntity.getUserId(),
				userEntity.getEmail(),
				userEntity.getUsername(),
				userEntity.isGender(),
				userEntity.getBirthDate(),
				userEntity.getHeight(),
				userEntity.getWeight(),
				userEntity.getTargetWeight()
				);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Integer getTargetWeight() {
		return targetWeight;
	}

	public void setTargetWeight(Integer targetWeight) {
		this.targetWeight = targetWeight;
	}
	


	
	
}
