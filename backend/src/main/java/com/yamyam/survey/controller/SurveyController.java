package com.yamyam.survey.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.survey.dto.QuestionResponse;
import com.yamyam.survey.dto.SurveyRequest;
import com.yamyam.persona.dto.PersonaResponse;
import com.yamyam.survey.service.SurveyService;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

	//생성자 주입
	private final SurveyService surveyService;
	public SurveyController(SurveyService surveyService) {
		this.surveyService = surveyService;
	}

	// 설문조사 목록 리스트 반환
	@GetMapping("/questions")
	public ResponseEntity<List<QuestionResponse>> questions(@RequestParam("stepLevel") int stepLevel) {
		return ResponseEntity.ok(surveyService.getAllQuestions(stepLevel));
	}
	
	// 설문조사 걸과 리스트 반환
	@GetMapping("/results")
	public ResponseEntity<List<PersonaResponse>> results() {
		return ResponseEntity.ok(surveyService.getAllResults());
	}
	
	
	//설문조사 결과 저장
	@PostMapping("/")
	public ResponseEntity<?> submit(@RequestBody SurveyRequest surveyRequest) {
		if(surveyService.insertSurveyResult(surveyRequest)) {
			return ResponseEntity.ok().build();
		}
		
		return ResponseEntity.badRequest().build();
	}
}
