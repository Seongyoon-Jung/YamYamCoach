package com.yamyam.user.service;

import com.yamyam.user.dto.SignUpRequest;
import com.yamyam.user.dto.UpdateRequest;
import com.yamyam.auth.dto.SecurityAccount;
import com.yamyam.user.dto.CurrentUserResponse;
import com.yamyam.user.dto.UserDetailResponse;

import jakarta.servlet.http.HttpSession;

public interface UserService {
	
	public boolean signUp(SignUpRequest userSignUpRequestDto);
	
	public boolean checkedEmail(String email);
	
	public boolean checkedUsername(String username);
	
	public UserDetailResponse checkUserDetail(SecurityAccount principal);
	
	public CurrentUserResponse checkCurrentUser(SecurityAccount principal);
	
	public boolean updateUserInfo(UpdateRequest updateRequest);
	
	public boolean deleteUser(HttpSession session, int userId);
}
