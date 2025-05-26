package com.yamyam.question.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class QuestionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	private int questionId;
	
	@Column(name = "step_level", nullable = false)
	private int stepLevel;
	
	@Column(name = "question_text", nullable = false)
	private String question_text;

	public String getQuestion_text() {
		return question_text;
	}	
	
}
