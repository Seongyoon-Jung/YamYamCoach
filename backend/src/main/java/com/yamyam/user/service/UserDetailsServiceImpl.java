package com.yamyam.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yamyam.auth.dto.SecurityAccount;
import com.yamyam.user.entity.UserEntity;
import com.yamyam.user.repository.UserRepository;


// AuthenticationManagerBuilder > userDetailsServiceImpl (직접 구현해야함) > loadUserByUsername()을 통해 인증절차 진행 됨.

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	//생성자 주입
	private final UserRepository userRepository;
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	// 우리 서비스에서 아이디는 email임
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity userData = userRepository.findByEmail(email);
		
		if(userData != null) {
			return new SecurityAccount(userData);
		}
		return null;
	}

}
