package com.yamyam.dto.response;

public class LoginResponse {
	private String username;
	private boolean isSurveyed;
	
	public LoginResponse() {
	}

	public LoginResponse(String username, boolean isSurveyed) {
		this.username = username;
		this.isSurveyed = isSurveyed;
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
	
}
