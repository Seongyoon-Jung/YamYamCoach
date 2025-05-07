package com.yamyam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	
	UserEntity findByUserId(int userId);
	
	// userdetailserviceImpl에서 Id값을 가져오기위한 메서드
	UserEntity findByEmail(String email);
	
	UserEntity findByUsername(String username);
	
	// 이메일 중복 확인 
	boolean existsByEmail(String email);
	
	// 닉네임 중복 확인
	boolean existsByUsername(String username);
}
