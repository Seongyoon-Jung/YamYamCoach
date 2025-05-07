package com.yamyam.dto.response;

import java.time.LocalDate;

public class SignUpResponseDto {
private String message;
	
	public SignUpResponseDto() {
	}

	public SignUpResponseDto(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}