package com.yamyam.dto.request;

public class SurveyRequest {
	private int userId;
	private int personaId;
	private int stepLevel;
	private String answerValues;
	
	public SurveyRequest() {
	}

	public SurveyRequest(int userId, int personaId, int stepLevel, String answerValues) {
		this.userId = userId;
		this.personaId = personaId;
		this.stepLevel = stepLevel;
		this.answerValues = answerValues;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getPersonaId() {
		return personaId;
	}

	public void setPersonaId(int personaId) {
		this.personaId = personaId;
	}

	public int getStepLevel() {
		return stepLevel;
	}

	public void setStepLevel(int stepLevel) {
		this.stepLevel = stepLevel;
	}

	public String getAnswerValues() {
		return answerValues;
	}

	public void setAnswerValues(String answerValues) {
		this.answerValues = answerValues;
	}
	
	
}
