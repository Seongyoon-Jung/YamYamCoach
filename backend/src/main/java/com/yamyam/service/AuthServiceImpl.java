package com.yamyam.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.yamyam.dto.request.LoginRequest;
import com.yamyam.dto.response.CurrentUserResponse;
import com.yamyam.entity.UserEntity;
import com.yamyam.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService{

	//생성자 주입 우리는 시큐리티 기본 formlogin을 비활성화 했기 때문에 여기서 커스텀으로 인가를 해야한다.
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
	}

	@Override
	public CurrentUserResponse login(LoginRequest loginRequestDto, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        // 스프링 시큐리티에 위임 → 내부적으로 UserDetailsService + PasswordEncoder 실행됨
		Authentication authentication = authenticationManager.authenticate(token);
        
		// 인증 정보를 SecurityContext에 저장 (★ 이게 있어야 세션에 저장됨)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // ✅ 세션에 인증 정보 저장 (이게 없으면 JSESSIONID만 있어도 인증된 사용자로 안 봄)
        request.getSession(true)
               .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                             SecurityContextHolder.getContext());
        Integer personaId;
        UserEntity userEntity = userRepository.findByEmail(loginRequestDto.getEmail());
        if(userEntity.getPersona() == null) {
        	personaId = null;
        }
        else{
        	personaId = userEntity.getPersona().getPersonaId();
        }
        
        
        CurrentUserResponse currentUserResponse = new CurrentUserResponse(userEntity.getUserId(), userEntity.getUsername(), personaId, userEntity.getRole());

        return currentUserResponse;
        
	}

}
