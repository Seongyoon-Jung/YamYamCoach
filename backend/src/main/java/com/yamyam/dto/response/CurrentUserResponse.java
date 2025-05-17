package com.yamyam.dto.response;

import com.yamyam.entity.PersonaEntity;

public class CurrentUserResponse {
	private int userId;
	private String username;
	private Integer personaId;
	private String role;
	
	public CurrentUserResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public CurrentUserResponse(int userId, String username, Integer personaId, String role) {
		this.userId = userId;
		this.username = username;
		this.personaId = personaId;
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

	@Override
	public String toString() {
		return "CurrentUserResponse [userId=" + userId + ", username=" + username + ", personaId=" + personaId
				+ ", role=" + role + "]";
	}
}
