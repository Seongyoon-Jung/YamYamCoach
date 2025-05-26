package com.yamyam.persona.dto;


public class PersonaResponse {
    private Integer personaId;
    private String name;
    private String diseaseTags;
    private String description;
    private String recommendation;
    
    public PersonaResponse() {
		// TODO Auto-generated constructor stub
	}

	public PersonaResponse(Integer personaId, String name, String diseaseTags, String description,
			String recommendation) {
		this.personaId = personaId;
		this.name = name;
		this.diseaseTags = diseaseTags;
		this.description = description;
		this.recommendation = recommendation;
	}

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
