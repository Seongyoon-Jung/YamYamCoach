package com.yamyam.survey.service;

import java.util.List;

import com.yamyam.survey.dto.QuestionResponse;
import com.yamyam.survey.dto.SurveyRequest;
import com.yamyam.persona.dto.PersonaResponse;
import com.yamyam.survey.entity.SurveyEntity;

public interface SurveyService {
	
	List<QuestionResponse> getAllQuestions(int stepLevel);
	
	List<PersonaResponse> getAllResults();
	
	boolean insertSurveyResult(SurveyRequest surveyRequest);
}
