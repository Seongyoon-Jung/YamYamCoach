package com.yamyam.survey.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.yamyam.user.entity.UserEntity;
import com.yamyam.persona.entity.PersonaEntity;

@Entity
@Table(name = "survey")
public class SurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Integer surveyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "step_level", nullable = false)
    private Integer stepLevel;

    @Column(name = "survey_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime surveyDate;

    @Column(name = "answer_values", nullable = false, length = 255)
    private String answerValues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private PersonaEntity persona;

    // 기본 생성자
    public SurveyEntity() {}

    // 전체 필드 생성자 (surveyDate는 자동으로 설정되므로 제외)
    public SurveyEntity(UserEntity user, Integer stepLevel, String answerValues, PersonaEntity persona) {
        this.user = user;
        this.stepLevel = stepLevel;
        this.answerValues = answerValues;
        this.persona = persona;
    }

    // PrePersist 메서드로 자동 시간 설정
    @PrePersist
    protected void onCreate() {
        this.surveyDate = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Integer getStepLevel() {
        return stepLevel;
    }

    public void setStepLevel(Integer stepLevel) {
        this.stepLevel = stepLevel;
    }

    public LocalDateTime getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(LocalDateTime surveyDate) {
        this.surveyDate = surveyDate;
    }

    public String getAnswerValues() {
        return answerValues;
    }

    public void setAnswerValues(String answerValues) {
        this.answerValues = answerValues;
    }

    public PersonaEntity getPersona() {
        return persona;
    }

    public void setPersona(PersonaEntity persona) {
        this.persona = persona;
    }
}
