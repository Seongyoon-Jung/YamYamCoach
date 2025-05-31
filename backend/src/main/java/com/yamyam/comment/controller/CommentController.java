package com.yamyam.comment.controller;

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

import com.yamyam.comment.dto.CommentRequest;
import com.yamyam.board.dto.BoardResponse;
import com.yamyam.comment.dto.CommentResponse;
import com.yamyam.board.service.BoardService;
import com.yamyam.comment.service.CommentService;

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
	
	// 댓글 삭제
	@DeleteMapping("")
	public ResponseEntity<?> deleteComment(@RequestBody CommentRequest request){
		commentService.delete(request);
		return ResponseEntity.ok().build();
	}
}
