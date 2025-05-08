package com.yamyam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "survey")
public class SurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Integer surveyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "step_level", nullable = false)
    private Integer stepLevel;

    @Column(name = "survey_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime surveyDate;

    @Column(name = "answer_values", nullable = false, length = 100)
    private String answerValues;

    // 기본 생성자
    public SurveyEntity() {
    }

    // 전체 필드 생성자
    public SurveyEntity(UserEntity user, Integer stepLevel, String answerValues) {
        this.user = user;
        this.stepLevel = stepLevel;
        this.answerValues = answerValues;
        this.surveyDate = LocalDateTime.now();
    }
}
