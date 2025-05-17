package com.yamyam.service;

import java.util.List;

import com.yamyam.dto.request.SurveyRequest;
import com.yamyam.dto.response.PersonaResponse;
import com.yamyam.dto.response.QuestionResponse;
import com.yamyam.entity.SurveyEntity;

public interface SurveyService {
	
	List<QuestionResponse> getAllQuestions(int stepLevel);
	
	List<PersonaResponse> getAllResults();
	
	boolean insertSurveyResult(SurveyRequest surveyRequest);
}
