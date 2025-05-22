package com.yamyam.service;

import java.util.List;

import com.yamyam.dto.request.BoardRequest;
import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;

public interface BoardService {
	// 게시글 전체 조회
	public List<BoardResponse> getAll();
	
	public void plusViewCount(int boardId);
	
	//게시글 상세보기
	public BoardResponse getBoard(int boardId);
	
	//게시글작성
	public void writeBoard(BoardRequest boardRequest, String imageUrl); 

	// 게시글 수정
	public void modifyBoard(BoardRequest boardRequest); 
	
	//게시글 삭제
	public void deleteBoard(int boardId);
}
