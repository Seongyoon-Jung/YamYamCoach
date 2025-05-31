package com.yamyam.user.dto;


public class CurrentUserResponse {
	private int userId;
	private String username;
	private Integer personaId;
	private String role;
	private String profileUrl;
	
	public CurrentUserResponse() {
		// TODO Auto-generated constructor stub
	}

	public CurrentUserResponse(int userId, String username, Integer personaId, String role, String profileUrl) {
		super();
		this.userId = userId;
		this.username = username;
		this.personaId = personaId;
		this.role = role;
		this.profileUrl = profileUrl;
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

	public Integer getPersonaId() {
		return personaId;
	}

	public void setPersonaId(Integer personaId) {
		this.personaId = personaId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	
}
