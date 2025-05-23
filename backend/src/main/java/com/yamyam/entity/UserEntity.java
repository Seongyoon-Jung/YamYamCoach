package com.yamyam.entity;

import java.time.LocalDate;

import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.request.UpdateRequest;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private boolean gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    private Integer height;
    private Integer weight;

    @Column(name = "target_weight")
    private Integer targetWeight;

    // 페르소나 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private PersonaEntity persona;

    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ROLE_USER'")
    private String role = "ROLE_USER";

    @Column(nullable = false, length = 255, columnDefinition = "VARCHAR(255) DEFAULT 'uploads/user/default-avatar.png'")
    private String profileUrl = "uploads/user/default-avatar.png";
    

    public UserEntity() {}

    public UserEntity(String email, String password, String username, boolean gender,
                      LocalDate birthDate, Integer height, Integer weight, Integer targetWeight) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.gender = gender;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
        this.targetWeight = targetWeight;
        this.role = "ROLE_USER";
        this.profileUrl = "https://yamyamcoach.s3.ap-northeast-2.amazonaws.com/uploads/user/default-avatar.png";
    }

    // Static Factory Method for Signup
    public static UserEntity signUpForm(SignUpRequest dto, String encodedPassword) {
        return new UserEntity(
            dto.getEmail(),
            encodedPassword,
            dto.getUsername(),
            dto.isGender(),
            dto.getBirthDate(),
            dto.getHeight(),
            dto.getWeight(),
            dto.getTargetWeight()
        );
    }

    // Update Method
    public void updateForm(UpdateRequest dto) {
        this.username = dto.getUsername();
        this.gender = dto.isGender();
        this.birthDate = dto.getBirthDate();
        this.height = dto.getHeight();
        this.weight = dto.getWeight();
        this.targetWeight = dto.getTargetWeight();
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Integer targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public PersonaEntity getPersona() {
        return persona;
    }

    public void setPersona(PersonaEntity persona) {
        this.persona = persona;
    }


	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	@Override
    public String toString() {
        return "UserEntity{" +
            "userId=" + userId +
            ", email='" + email + '\'' +
            ", username='" + username + '\'' +
            ", gender=" + gender +
            ", birthDate=" + birthDate +
            ", height=" + height +
            ", weight=" + weight +
            ", targetWeight=" + targetWeight +
            ", role='" + role + '\'' +
            '}';
    }
}
