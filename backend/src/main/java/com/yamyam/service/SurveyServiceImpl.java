package com.yamyam.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yamyam.dto.request.SurveyRequest;
import com.yamyam.dto.response.QuestionResponse;
import com.yamyam.entity.QuestionEntity;
import com.yamyam.entity.SurveyEntity;
import com.yamyam.entity.UserEntity;
import com.yamyam.repository.QuestionRepository;
import com.yamyam.repository.SurveyRepository;
import com.yamyam.repository.UserRepository;

@Service
public class SurveyServiceImpl implements SurveyService{
	
	//생성자 주입
	private final QuestionRepository questionRepository;
	private final SurveyRepository surveyRepository;
	private final UserRepository userRepository;
	public SurveyServiceImpl(QuestionRepository questionRepository, SurveyRepository surveyRepository,
			UserRepository userRepository) {
		this.questionRepository = questionRepository;
		this.surveyRepository = surveyRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<QuestionResponse> getAllQuestions(int stepLevel) {
		List<QuestionEntity> list = questionRepository.findAllByStepLevel(stepLevel);
		List<QuestionResponse> responseList = new ArrayList<>();
		for (QuestionEntity questionEntity : list) {
			responseList.add(new QuestionResponse(questionEntity.getQuestion_text()));
		}
		return responseList;
	}

	@Override
	public boolean insertSurveyResult(SurveyRequest surveyRequest) {
		try {
			UserEntity userEntity = userRepository.findByUserId(surveyRequest.getUserId());
			
			SurveyEntity surveyEntity = new SurveyEntity(userEntity, surveyRequest.getStepLevel(), surveyRequest.getAnswerValues());
			
			surveyRepository.save(surveyEntity);	
			userEntity.setSurveyed(true);
			userEntity.setDietType(surveyRequest.getDietType());
			userRepository.save(userEntity);
			return true;
		}
		catch(Exception e) {
			return false;
		}
		
	}
	

}
