package com.yamyam.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;
import com.yamyam.service.BoardService;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	private final BoardService boardService;
	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	// 게시판 글 조회
	@GetMapping("")
	public ResponseEntity<List<BoardResponse>> getBoardList(){
		return ResponseEntity.ok(boardService.getAll());
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
	@GetMapping("/{boardId}")
	public ResponseEntity<BoardResponse> getBoard(@PathVariable("boardId") int boardId) {
		
		return ResponseEntity.ok(boardService.getBoard(boardId));
	}
}
