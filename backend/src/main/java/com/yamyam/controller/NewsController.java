package com.yamyam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class NewsController {

	@GetMapping("")
	public ResponseEntity<?> getAllNews(){
		
		
		
		return null;
	}
	
	@GetMapping("/{personaId}")
	public ResponseEntity<?> getAllPersonaNews(@PathVariable("personaId") int personaId){
		System.out.println("personaId"+personaId);
		return null;
	}
}
