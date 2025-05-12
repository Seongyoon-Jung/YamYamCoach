package com.yamyam.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.dto.SecurityAccount;
import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.request.UpdateRequest;
import com.yamyam.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	// 생성자 주입
	private final UserService userService;
	public UserController(UserService userService) {
		this.userService = userService;
	}

	//내 정보 상세조회
	@GetMapping("")
	public ResponseEntity<?> detail(@AuthenticationPrincipal SecurityAccount principal) {
		if(principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		return ResponseEntity.ok(userService.checkUserDetail(principal));
	}
	
	// 회원가입
	@PostMapping("")
	public ResponseEntity<String> singup(@RequestBody SignUpRequest dto) {
		userService.signUp(dto);
		return ResponseEntity.ok("회원가입 성공");
	}
	
	// 회원 정보 수정
	@PutMapping("")
	public ResponseEntity<String> update(@RequestBody UpdateRequest updateRequest){
		userService.updateUserInfo(updateRequest);
		return ResponseEntity.ok("개인정보 수정 성공");
	}
	
	//회원 탈퇴
	@DeleteMapping("")
	public ResponseEntity<String> delete(HttpSession session, @RequestParam("userId") int userId){
		userService.deleteUser(session,userId);
		return ResponseEntity.ok("회원탈퇴 성공");
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
	// @AuthenticationPrincipal SecurityAccount principal 현재 로그인 정보를 불러오기 위한 것
	public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal SecurityAccount principal) {
		if(principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		return ResponseEntity.ok(userService.checkCurrentUser(principal));
	
	}
}
