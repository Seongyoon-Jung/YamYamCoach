package com.yamyam.dto.response;

import java.time.LocalDateTime;

public class NewsResponse {
	private LocalDateTime date;
	private String title;
	private String description;
	private String newsUrl;
	private String imageUrl;
	private String tags;
	
	public NewsResponse() {
	}

	public NewsResponse(LocalDateTime date, String title, String description, String newsUrl, String imageUrl,
			String tags) {
		this.date = date;
		this.title = title;
		this.description = description;
		this.newsUrl = newsUrl;
		this.imageUrl = imageUrl;
		this.tags = tags;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "NewsResponse [date=" + date + ", title=" + title + ", description=" + description + ", newsUrl="
				+ newsUrl + ", imageUrl=" + imageUrl + ", tags=" + tags + "]";
	}
	
	
	
}
