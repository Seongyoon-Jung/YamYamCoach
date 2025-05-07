package com.yamyam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.entity.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer>{
	
	//질문리스트를 뽑기 위한 메서드
	List<QuestionEntity> findAllByStepLevel(int stepLevel);
	
	
}
