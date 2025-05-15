package com.yamyam.service;

import java.util.List;

import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;

public interface BoardService {
	// 게시글 전체 조회
	public List<BoardResponse> getAll();
	
	//게시글 상세보기
	public BoardResponse getBoard(int boardId);

}
