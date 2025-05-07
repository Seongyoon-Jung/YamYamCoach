package com.yamyam.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.dto.SecurityAccount;
import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.response.LoginResponse;
import com.yamyam.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	// 생성자 주입
	private final UserService userService;
	public UserController(UserService userService) {
		this.userService = userService;
	}

	//회원가입
	@PostMapping("")
	public ResponseEntity<String> singup(@RequestBody SignUpRequest dto) {
		System.out.println(dto.toString());
		userService.signUp(dto);
		return ResponseEntity.ok("회원가입 성공");
	}
	
	//이메일 중복 검사
	@GetMapping("/check-email")
	public ResponseEntity<?> checkEmail(@RequestParam("email") String email){
		// 중복 안된 경우
		if(!userService.checkedEmail(email)) {
			return ResponseEntity.ok("email can use");
		}
		
		return ResponseEntity.badRequest().build();
	}
	
	//이메일 중복 검사
	@GetMapping("/check-username")
	public ResponseEntity<String> checkUsername(@RequestParam("username") String username){
		// 중복 안된 경우
		if(!userService.checkedUsername(username)) {
			return ResponseEntity.ok("username can use");
		}
		
		return ResponseEntity.badRequest().build();
	}
	
	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null || !auth.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	    
	    Object principal = auth.getPrincipal();
	    //로그인 정보에 isSurveyed 를 담기위해서 새로 정의
	    if (principal instanceof SecurityAccount) {
	        SecurityAccount account = (SecurityAccount) principal;
	        boolean isSurveyed = account.isSurveyed();
	        LoginResponse loginResponse = new LoginResponse(account.getUsername(),account.isSurveyed());
	        
	        return ResponseEntity.ok(loginResponse);
	    }
	    
	    return ResponseEntity.ok(auth.getName() ); // 또는 사용자 정보 DTO
	}
}
