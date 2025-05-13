package com.yamyam.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
public class BoardController {

	// 게시판 글 조회
	@GetMapping("")
	public void getBoardList(){
		
	}
	
	// 게시판 글 작성
	@PostMapping("")
	public void writeBoard() {
		
	}
	
	// 게시판 글 수정
	@PutMapping("")
	public void modifyBoard() {
		
	}
	
	// 게시판 글 삭제
	@DeleteMapping("")
	public void deleteBoard() {
		
	}
	
	// 게시판 글 상세 조회
	@GetMapping("{board_id}")
	public void getBoard() {
		
	}
}
