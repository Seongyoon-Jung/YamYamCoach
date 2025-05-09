package com.yamyam.dto.request;

import java.time.LocalDate;

public class SignUpRequest {
    private String email;
    private String password;  // 입력 비밀번호, 서버에서 hash 처리 예정
    private String username;
    private boolean gender;
    private LocalDate birthDate;
    // null 값이 들어갈 수도 있으므로 참조형으로 선언
    private Integer height;
    private Integer weight;
    private Integer targetWeight;

    public SignUpRequest() {
	}

	public SignUpRequest(String email, String password, String username, boolean gender, LocalDate birthDate,
			Integer height, Integer weight, Integer targetWeight) {
		this.email = email;
		this.password = password;
		this.username = username;
		this.gender = gender;
		this.birthDate = birthDate;
		this.height = height;
		this.weight = weight;
		this.targetWeight = targetWeight;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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