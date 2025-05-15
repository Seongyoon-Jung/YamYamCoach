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
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.dto.request.CommentRequest;
import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;
import com.yamyam.service.BoardService;
import com.yamyam.service.CommentService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
	private final CommentService commentService;
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	// 게시판에 맞는 댓글 전체 조회
	@GetMapping("/{boardId}")
	public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable("boardId") int boardId) {
		
		return ResponseEntity.ok(commentService.getAllComment(boardId));
	}
	
	// 댓글 작성
	@PostMapping("")
	public ResponseEntity<CommentResponse> saveComment(@RequestBody CommentRequest request){
		return ResponseEntity.ok(commentService.save(request));
	}
	
	// 댓글 수정
	@PutMapping("")
	public ResponseEntity<CommentResponse> modfiyComment(@RequestBody CommentRequest request){
		
		return ResponseEntity.ok(commentService.modify(request));
	}
}
