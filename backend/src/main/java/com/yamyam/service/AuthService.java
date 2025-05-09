package com.yamyam.service;

import com.yamyam.dto.request.LoginRequest;
import com.yamyam.dto.response.CurrentUserResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

	public CurrentUserResponse login(LoginRequest loginRequestDto, HttpServletRequest request);
}
