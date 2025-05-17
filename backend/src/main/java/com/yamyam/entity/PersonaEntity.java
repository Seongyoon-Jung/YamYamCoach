package com.yamyam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "persona")
public class PersonaEntity {

    @Id
    @Column(name = "persona_id", length = 10)
    private Integer personaId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "disease_tags", length = 100)
    private String diseaseTags;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String recommendation;

    // 기본 생성자
    public PersonaEntity() {}

    // 전체 필드 생성자
    public PersonaEntity(Integer personaId, String name, String diseaseTags, String description, String recommendation) {
        this.personaId = personaId;
        this.name = name;
        this.diseaseTags = diseaseTags;
        this.description = description;
        this.recommendation = recommendation;
    }

    // Getters and Setters

    public Integer getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Integer personaId) {
        this.personaId = personaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiseaseTags() {
        return diseaseTags;
    }

    public void setDiseaseTags(String diseaseTags) {
        this.diseaseTags = diseaseTags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
