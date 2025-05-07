package com.yamyam.dto.request;

import java.time.LocalDate;

public class UpdateRequest {
	private boolean gender;
    private LocalDate birthDate;
    private int height;
    private int weight;
    private int targetWeight;
    private boolean isSurveyed;
    private String dietType;
    
    public UpdateRequest() {
	}

	public UpdateRequest(boolean gender, LocalDate birthDate, int height, int weight, int targetWeight,
			boolean isSurveyed, String dietType) {
		this.gender = gender;
		this.birthDate = birthDate;
		this.height = height;
		this.weight = weight;
		this.targetWeight = targetWeight;
		this.isSurveyed = isSurveyed;
		this.dietType = dietType;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getTargetWeight() {
		return targetWeight;
	}

	public void setTargetWeight(int targetWeight) {
		this.targetWeight = targetWeight;
	}

	public boolean isSurveyed() {
		return isSurveyed;
	}

	public void setSurveyed(boolean isSurveyed) {
		this.isSurveyed = isSurveyed;
	}

	public String getDietType() {
		return dietType;
	}

	public void setDietType(String dietType) {
		this.dietType = dietType;
	}

	@Override
	public String toString() {
		return "UpdateRequest [gender=" + gender + ", birthDate=" + birthDate + ", height=" + height + ", weight="
				+ weight + ", targetWeight=" + targetWeight + ", isSurveyed=" + isSurveyed + ", dietType=" + dietType
				+ "]";
	}
	
}
