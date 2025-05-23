package com.yamyam.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yamyam.dto.request.BoardRequest;
import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;
import com.yamyam.service.BoardService;
import com.yamyam.service.S3UploadService;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	private final BoardService boardService;
	private final S3UploadService s3UploadService;
	public BoardController(BoardService boardService, S3UploadService s3UploadService) {
		this.boardService = boardService;
		this.s3UploadService = s3UploadService;
	}

	// 게시판 글 조회
	@GetMapping("")
	public ResponseEntity<List<BoardResponse>> getBoardList(){		
		return ResponseEntity.ok(boardService.getAll());
	}

	@PostMapping("")
	public void writeBoard(@RequestBody BoardRequest boardRequest) {
	    // imageUrl을 boardRequest에 담거나, 따로 저장 처리
	    boardService.writeBoard(boardRequest);
	}
	
	// 게시판 글 수정
	@PutMapping("")
	public void modifyBoard(@RequestPart("board") BoardRequest boardRequest, @RequestPart(value = "file", required = false) MultipartFile file) {
	    String imageUrl = boardRequest.getImageUrl(); // 기본은 기존 이미지 URL 유지
	    
	    if (file != null && !file.isEmpty()) {
	        try {
	            // 기존 이미지 삭제
	            if (imageUrl != null && !imageUrl.isEmpty()) {
	            	System.out.println("delete============================");
	                s3UploadService.deleteImage(imageUrl);
	            }

	            // 새 이미지 업로드
	            imageUrl = s3UploadService.saveFile(file, "board");
	        } catch (IOException e) {
	            throw new RuntimeException("파일 수정 업로드 중 오류 발생: " + e.getMessage());
	        }
	    }

	    boardService.modifyBoard(boardRequest,imageUrl);
	}

	
	// 게시판 글 삭제
	@DeleteMapping("")
	public void deleteBoard(@RequestBody BoardRequest boardRequest) {
		boardService.deleteBoard(boardRequest.getBoardId());
		s3UploadService.deleteImage(boardRequest.getImageUrl());
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
