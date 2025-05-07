package com.yamyam.dto.request;

import java.time.LocalDate;

public class SignUpRequest {
    private String email;
    private String password;  // 입력 비밀번호, 서버에서 hash 처리 예정
    private String username;
    private boolean gender;
    private LocalDate birthDate;
    private int height;
    private int weight;

    public SignUpRequest() {
	}

	public SignUpRequest(String email, String password, String username, boolean gender, LocalDate birthDate,
			int height, int weight) {
		this.email = email;
		this.password = password;
		this.username = username;
		this.gender = gender;
		this.birthDate = birthDate;
		this.height = height;
		this.weight = weight;
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

	@Override
	public String toString() {
		return "UserSignUpDto [email=" + email + ", password=" + password + ", username=" + username + ", gender="
				+ gender + ", birthDate=" + birthDate + ", height=" + height + ", weight=" + weight + "]";
	}
	
}