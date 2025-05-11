// UserEntity.java
package com.yamyam.entity;

import java.time.LocalDate;

import com.yamyam.dto.request.SignUpRequest;
import com.yamyam.dto.request.UpdateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// @Entity : Entity로써 빈에 등록
@Entity

// 클래스명이 DB의 테이블 명과 다를때 테이블 명을 꼭 지정해줘야한다.
// 클래스명 : UserEntity, 테이블 명 : user 이므로 name 지정 해줘야함
@Table(name = "user")
public class UserEntity {

	@Id // 기본키로 설정할 것 이다.
	
	// 기본값을 DB에서 자동으로 생성
	// GenerationType.IDENTITY : 기본 키 생성을 DB에 위임 (Mysql) 할 것
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column // 객체 필드를 테이블 컬럼과 매핑한다. jpa에서는 카멜케이스를 자동으로 DB에서 스네이크 케이스로 변경해줌
    private int userId; // Integer → int 변경

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    // Bcrypt로 해싱한 문자열은 항상 60자리이다!
    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private boolean gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight")
    private Integer weight;
    
    @Column(name="target_weight")
    private Integer targetWeight;

    @Column(name = "is_surveyed")
    private boolean isSurveyed = false;

    @Column(name = "diet_type", length = 50)
    private String dietType;

    @Column(name = "role", length = 20, nullable = false)
    private String role = "USER";
    
    // 기본 생성자 (필수적으로 필요)
    public UserEntity() {}
    
    public UserEntity(String email, String password, String username, boolean gender, LocalDate birthDate,
			Integer height, Integer weight, Integer targetWeight) {
		this.email = email;
		this.password = password;
		this.username = username;
		this.gender = gender;
		this.birthDate = birthDate;
		this.height = height;
		this.weight = weight;
		this.targetWeight = targetWeight;
		this.role = "ROLE_USER";
	}
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	//회원가입 시 setter
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
    
    //회원 정보 수정 setter
    public void updateForm(UpdateRequest dto) {
    	this.username = dto.getUsername();
    	this.gender = dto.isGender();
    	this.birthDate = dto.getBirthDate();
    	this.height = dto.getHeight();
    	this.weight = dto.getWeight();
    	this.targetWeight = dto.getTargetWeight();
    }
    
	@Override
	public String toString() {
		return "UserEntity [userId=" + userId + ", email=" + email + ", password=" + password + ", username=" + username
				+ ", gender=" + gender + ", birthDate=" + birthDate + ", height=" + height + ", weight=" + weight
				+ ", isSurveyed=" + isSurveyed + ", dietType=" + dietType + ", role=" + role + "]";
	}
    
    
}
