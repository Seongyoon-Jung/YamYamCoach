package com.yamyam.service;

import java.util.List;

import com.yamyam.dto.response.NewsResponse;

public interface NewsService {
	List<NewsResponse> getAllNews();
	
	List<NewsResponse> getAllPersonaNews(int personaId);
}
