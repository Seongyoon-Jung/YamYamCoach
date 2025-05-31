package com.yamyam.diet.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "course_schedule",
		uniqueConstraints = {
			   @UniqueConstraint(columnNames = { "schedule_date", "course_type" })
    		}
		)
public class CourseSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleId;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    @Column(nullable = false, length = 1)
    private String courseType;

    @Column(nullable = false, length = 100)
    private String courseName;

    @Column(name = "img_url")
    private String imgUrl;

    @OneToMany(mappedBy = "courseSchedule", cascade = CascadeType.ALL)
    private List<ScheduleDish> scheduleDishes;

    public CourseSchedule() {
		// TODO Auto-generated constructor stub
	}

	public CourseSchedule(LocalDate scheduleDate, String courseType, String courseName, String imgUrl) {
		super();
		this.scheduleDate = scheduleDate;
		this.courseType = courseType;
		this.courseName = courseName;
		this.imgUrl = imgUrl;
	}

	// Getters
    public int getScheduleId() {
        return scheduleId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public String getCourseType() {
        return courseType;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<ScheduleDish> getScheduleDishes() {
        return scheduleDishes;
    }

    // Setters
    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setScheduleDishes(List<ScheduleDish> scheduleDishes) {
        this.scheduleDishes = scheduleDishes;
    }
} 