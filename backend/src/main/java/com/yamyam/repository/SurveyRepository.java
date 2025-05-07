package com.yamyam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.entity.SurveyEntity;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Integer>{
	
	
}
