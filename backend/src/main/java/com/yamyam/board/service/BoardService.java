package com.yamyam.board.service;

import java.util.List;

import com.yamyam.board.dto.BoardRequest;
import com.yamyam.board.dto.BoardResponse;
import com.yamyam.comment.dto.CommentResponse;

public interface BoardService {
	// 게시글 전체 조회
	public List<BoardResponse> getAll();
	
	public void plusViewCount(int boardId);
	
	//게시글 상세보기
	public BoardResponse getBoard(int boardId);
	
	//게시글작성
	public void writeBoard(BoardRequest boardRequest); 

	// 게시글 수정
	public void modifyBoard(BoardRequest boardRequest, String imageUrl); 
	
	//게시글 삭제
	public void deleteBoard(int boardId);
}
