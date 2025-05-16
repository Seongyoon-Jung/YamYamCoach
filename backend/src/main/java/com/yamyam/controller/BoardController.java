package com.yamyam.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.dto.request.BoardRequest;
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
	public void writeBoard(@RequestBody BoardRequest boardRequest) {
		boardService.writeBoard(boardRequest);
	}
	
	// 게시판 글 수정
	@PutMapping("")
	public void modifyBoard(@RequestBody BoardRequest boardRequest) {
		boardService.modifyBoard(boardRequest);
	}
	
	// 게시판 글 삭제
	@DeleteMapping("")
	public void deleteBoard(@RequestBody BoardRequest boardRequest) {
		boardService.deleteBoard(boardRequest.getBoardId());
	}
	
	// 게시판 글 상세 조회
	@GetMapping("/{boardId}")
	public ResponseEntity<BoardResponse> getBoard(@PathVariable("boardId") int boardId, @RequestParam("hit") boolean hit) {
		// 수정 페이지로 갈때 조회수 안올리기 위해서 쿼리로 정보를 받아서 true일때만 조회수 증가하기
		if(hit) {
			//조회수 올리기
			boardService.plusViewCount(boardId);
		}
		
		return ResponseEntity.ok(boardService.getBoard(boardId));
	}
}
