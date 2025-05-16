package com.yamyam.dto.response;

public class CurrentUserResponse {
	private int userId;
	private String username;
	private boolean isSurveyed;
	private String role;
	
	public CurrentUserResponse() {
		// TODO Auto-generated constructor stub
	}

	public CurrentUserResponse(int userId, String username, boolean isSurveyed, String role) {
		this.userId = userId;
		this.username = username;
		this.isSurveyed = isSurveyed;
		this.role = role;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "CurrentUserResponse [userId=" + userId + ", username=" + username + ", isSurveyed=" + isSurveyed
				+ ", role=" + role + "]";
	}
	
	
	
	
}
