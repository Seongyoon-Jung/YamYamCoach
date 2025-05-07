package com.yamyam.dto.request;

public class SurveyRequest {
	private int userId;
	private String dietType;
	private int stepLevel;
	private String answerValues;
	
	public SurveyRequest() {
	}

	public SurveyRequest(int userId, String dietType, int stepLevel, String answerValues) {
		this.userId = userId;
		this.dietType = dietType;
		this.stepLevel = stepLevel;
		this.answerValues = answerValues;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getDietType() {
		return dietType;
	}

	public void setDietType(String dietType) {
		this.dietType = dietType;
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
