package com.yamyam.auth.service;


import com.yamyam.user.dto.CurrentUserResponse;
import com.yamyam.user.dto.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

	public CurrentUserResponse login(LoginRequest loginRequestDto, HttpServletRequest request);
}
