package com.yamyam.news.service;

import java.util.List;

import com.yamyam.news.dto.NewsResponse;

public interface NewsService {
	List<NewsResponse> getAllNews();
	
	List<NewsResponse> getAllPersonaNews(int personaId);
}
