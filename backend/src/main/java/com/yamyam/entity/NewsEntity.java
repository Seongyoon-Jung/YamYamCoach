package com.yamyam.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "news")
public class NewsEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Integer newsId;

    @Column(name="title", nullable = false, length = 50)
    private String title;

    @Column(name="description", nullable = false)
    private String description;
    
    @Column(name="tags", nullable = false)
    private String tags;
    
    @Column(name = "news_url",nullable = false)
    private String newsUrl;

    @Column(name = "image_url",nullable = false)
    private String imageUrl;
    
    @Column(name="date", nullable = false)
    private LocalDateTime date;
    
    public NewsEntity() {
	}

	public NewsEntity(String title, String description, String tags, String newsUrl, String imageUrl, LocalDateTime date) {
		this.title = title;
		this.description = description;
		this.tags = tags;
		this.newsUrl = newsUrl;
		this.imageUrl = imageUrl;
		this.date = date;
	}

	public Integer getNewsId() {
		return newsId;
	}

	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
    
    
}
