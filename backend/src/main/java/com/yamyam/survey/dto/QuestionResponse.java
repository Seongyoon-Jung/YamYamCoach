package com.yamyam.survey.dto;

public class QuestionResponse {
	private String questionText;
	
	public QuestionResponse() {
	}

	public QuestionResponse(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	
	
}
