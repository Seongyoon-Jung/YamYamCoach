package com.yamyam.service;


import com.yamyam.dto.SecurityAccount;
import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.request.UpdateRequest;
import com.yamyam.dto.response.CurrentUserResponse;
import com.yamyam.dto.response.UserDetailResponse;

public interface UserService {
	
	public boolean signUp(SignUpRequest userSignUpRequestDto);
	
	public boolean checkedEmail(String email);
	
	public boolean checkedUsername(String username);
	
	public UserDetailResponse checkUserDetail(SecurityAccount principal);
	
	public CurrentUserResponse checkCurrentUser(SecurityAccount principal);
	
	public boolean updateUserInfo(UpdateRequest updateRequest, String username);
}
