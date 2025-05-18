package com.yamyam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록된다
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http
			.csrf(csrf -> csrf.disable())                     // 🔒 CSRF 비활성화 (API 보안 방식에 따라)
			.cors(cors -> {}) // CORS 허용
			.formLogin(form -> form.disable())   			  // 🔒 폼 로그인 비활성화 (JWT 또는 SPA 연동 시)
			
			.authorizeHttpRequests((auth) -> auth
						.requestMatchers("/","/api/auth/login","/api/users","/api/users/check-email","/api/users/check-username","/api/board").permitAll()// /,/login은 모든 사용자가 접근 가능
						.requestMatchers("/api/health-log/**").permitAll() // health-log API는 모든 사용자 접근 가능 (테스트용)
						.requestMatchers("/my/**").hasAnyRole("USER") // my/ 뒤에 페이지는 사용자가 접근 가능
						.anyRequest().authenticated() // 위에 두개의 이외는 전부 불가능
						)
			.logout(logout -> logout
			          .logoutUrl("/api/auth/logout")            // 로그아웃 처리 URL
			          .invalidateHttpSession(true)              // 세션 무효화
			          .deleteCookies("JSESSIONID")              // JSESSIONID 쿠키 삭제
			      //  ↓ Redirect 를 없애고 200 OK 만 반환
			          .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
			      );
		
		return http.build();
	}
	
	// BCrypt 함수로 비밀번호를 해시 하도록 하는 함수
	// 어디더든 사용할 수 있도록 빈에 등록 함
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	
	// 우리는 formlogin을 사용하지 않고 json으로 받아서 할 것이기 때문에 따로 연결해줘야한다. 빈에 미리 등록
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
