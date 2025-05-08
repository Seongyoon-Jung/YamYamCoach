package com.yamyam.service;


import com.yamyam.dto.CurrentUser;
import com.yamyam.dto.SecurityAccount;
import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.request.UpdateRequest;

public interface UserService {
	
	public boolean signUp(SignUpRequest userSignUpRequestDto);
	
	public boolean checkedEmail(String email);
	
	public boolean checkedUsername(String username);
	
	public CurrentUser checkNowUser(SecurityAccount principal);
	
	public boolean updateUserInfo(UpdateRequest updateRequest, String username);
}
