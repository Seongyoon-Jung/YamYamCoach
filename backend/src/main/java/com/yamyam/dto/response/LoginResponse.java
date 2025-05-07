package com.yamyam.dto.response;

public class LoginResponse {
	private int userId;
	private String username;
	private boolean isSurveyed;
	
	public LoginResponse() {
	}

	public LoginResponse(int userId, String username, boolean isSurveyed) {
		this.userId = userId;
		this.username = username;
		this.isSurveyed = isSurveyed;
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

	public boolean isSurveyed() {
		return isSurveyed;
	}

	public void setSurveyed(boolean isSurveyed) {
		this.isSurveyed = isSurveyed;
	}

	@Override
	public String toString() {
		return "LoginResponse [userId=" + userId + ", username=" + username + ", isSurveyed=" + isSurveyed + "]";
	}

	
	
	
}
