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

    @OneToMany(mappedBy = "courseSchedule", cascade = CascadeType.ALL)
    private List<ScheduleDish> scheduleDishes;

    public CourseSchedule() {
		// TODO Auto-generated constructor stub
	}
    
    public CourseSchedule(LocalDate scheduleDate, String courseType, String courseName) {
		this.scheduleDate = scheduleDate;
		this.courseType = courseType;
		this.courseName = courseName;
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

    public void setScheduleDishes(List<ScheduleDish> scheduleDishes) {
        this.scheduleDishes = scheduleDishes;
    }
} 