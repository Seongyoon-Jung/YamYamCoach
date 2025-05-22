package com.yamyam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.yamyam.dto.request.SurveyRequest;
import com.yamyam.dto.response.PersonaResponse;
import com.yamyam.dto.response.QuestionResponse;
import com.yamyam.entity.PersonaEntity;
import com.yamyam.entity.QuestionEntity;
import com.yamyam.entity.SurveyEntity;
import com.yamyam.entity.UserEntity;
import com.yamyam.repository.PersonaRepository;
import com.yamyam.repository.QuestionRepository;
import com.yamyam.repository.SurveyRepository;
import com.yamyam.repository.UserRepository;

@Service
public class SurveyServiceImpl implements SurveyService{
	
	//생성자 주입
	private final QuestionRepository questionRepository;
	private final PersonaRepository personaRepository;
	private final SurveyRepository surveyRepository;
	private final UserRepository userRepository;
	public SurveyServiceImpl(QuestionRepository questionRepository,PersonaRepository personaRepository, SurveyRepository surveyRepository,
			UserRepository userRepository) {
		this.questionRepository = questionRepository;
		this.personaRepository = personaRepository;
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
	public List<PersonaResponse> getAllResults() {
		List<PersonaEntity> list = personaRepository.findAll();
		List<PersonaResponse> personaList = new ArrayList<>();
		for (PersonaEntity personaEntity : list) {
			personaList.add(new PersonaResponse(personaEntity.getPersonaId(), personaEntity.getName(),personaEntity.getDiseaseTags(),personaEntity.getDescription(),personaEntity.getRecommendation()));
		}
		return personaList;
		
	}
	
	@Override
	public boolean insertSurveyResult(SurveyRequest surveyRequest) {
		try {
			UserEntity userEntity = userRepository.findByUserId(surveyRequest.getUserId());
			PersonaEntity personaEntity = personaRepository.findByPersonaId(surveyRequest.getPersonaId());
			
			
			
			SurveyEntity surveyEntity = new SurveyEntity(userEntity, surveyRequest.getStepLevel(), surveyRequest.getAnswerValues(), personaEntity);
			
			surveyRepository.save(surveyEntity);
			
			// 유저에 데이터 변경
			userEntity.setPersona(personaEntity);
			userEntity.setProfileUrl("https://yamyamcoach.s3.ap-northeast-2.amazonaws.com/uploads/user/"+ personaEntity.getPersonaId()+".png");
			userRepository.save(userEntity);
			return true;
		}
		catch(Exception e) {
			return false;
		}
		
	}
}
