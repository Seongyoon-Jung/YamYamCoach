package com.yamyam.service;

import org.springframework.http.ResponseEntity;

import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.request.UpdateRequest;
import com.yamyam.dto.response.LoginResponse;
import com.yamyam.dto.response.SignUpResponseDto;

public interface UserService {
	
	public boolean signUp(SignUpRequest userSignUpRequestDto);
	
	public boolean checkedEmail(String email);
	
	public boolean checkedUsername(String username);
	
	public LoginResponse checkNowUser();
	
	public boolean updateUserInfo(UpdateRequest updateRequest, String username);
}
