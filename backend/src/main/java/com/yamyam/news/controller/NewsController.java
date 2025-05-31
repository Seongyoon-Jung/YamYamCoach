package com.yamyam.news.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.news.dto.NewsResponse;
import com.yamyam.news.service.NewsService;


@RestController
@RequestMapping("/api/news")
public class NewsController {
	private final NewsService newsService;
	public NewsController(NewsService newsService) {
		this.newsService = newsService;
	}

	@GetMapping("")
	public ResponseEntity<List<NewsResponse>> getAllNews(){
		
		return ResponseEntity.ok(newsService.getAllNews());
	}
	
	@GetMapping("/{personaId}")
	public ResponseEntity<List<NewsResponse>> getAllPersonaNews(@PathVariable("personaId") int personaId){
		return ResponseEntity.ok(newsService.getAllPersonaNews(personaId));
	}
	
	
}
