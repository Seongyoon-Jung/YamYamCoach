package com.yamyam.auth.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.yamyam.persona.entity.PersonaEntity;
import com.yamyam.user.entity.UserEntity;


// 로그인이 완료되면 정보를 담아 보낼 dto
public class SecurityAccount implements UserDetails{

	//생성자 직접 만들어 줘야함 (UserDetailServiceImpl에서 UserEntity값을 받아서 초기화 해줄 것 이므로)
	private UserEntity userEntity;
	
	public SecurityAccount(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	// 설정한 ROLE을 반환해 주는 메서드
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				// TODO Auto-generated method stub
				return userEntity.getRole();
			}
		});
		
 		return collection;
	}

	@Override
	public String getPassword() {
		return userEntity.getPassword();
	}

	
	//로그인 아이디를 리턴하는 것 같음
	// auth.getName하면 이 값에 들어감
	@Override
	public String getUsername() {
		return userEntity.getUsername();
	}
	
	public String getEmail() {
		return userEntity.getEmail();
	}
	
	public int getUserId() {
		return userEntity.getUserId();
	}
	
	public PersonaEntity getPersona() {
		return userEntity.getPersona();
	}
	
}
