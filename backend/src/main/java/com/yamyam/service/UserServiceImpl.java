package com.yamyam.service;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.yamyam.dto.CurrentUser;
import com.yamyam.dto.SecurityAccount;
import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.request.UpdateRequest;
import com.yamyam.dto.response.LoginResponse;
import com.yamyam.entity.UserEntity;
import com.yamyam.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{

	// 생성자 주입
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public boolean signUp(SignUpRequest userSignUpRequestDto) {
		
		UserEntity data = UserEntity.signUpForm(userSignUpRequestDto, bCryptPasswordEncoder.encode(userSignUpRequestDto.getPassword()));
		
		System.out.println(data);
		
		userRepository.save(data);
		
		return true;
	}

	@Override
	public boolean checkedEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean checkedUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public CurrentUser checkNowUser(SecurityAccount principal) {
	    String email = principal.getEmail();
	    
	    UserEntity userEntity = userRepository.findByEmail(email);
        
        return CurrentUser.currentUser(userEntity);
	    
	}

	@Override
	public boolean updateUserInfo(UpdateRequest updateRequest, String username) {
		UserEntity userEntity = userRepository.findByUsername(username);
//		private boolean gender;
//	    private LocalDate birthDate;
//	    private int height;
//	    private int weight;
//	    private int targetWeight;
//	    private boolean isSurveyed;
//	    private String dietType;
		
		return false;
	}

}
