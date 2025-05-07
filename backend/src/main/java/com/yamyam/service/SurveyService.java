package com.yamyam.service;

import java.util.List;

import com.yamyam.dto.response.QuestionResponse;

public interface SurveyService {
	
	List<QuestionResponse> getAllQuestions(int stepLevel);
}
