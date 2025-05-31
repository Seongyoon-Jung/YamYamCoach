package com.yamyam.comment.service;

import java.util.List;

import com.yamyam.comment.dto.CommentRequest;
import com.yamyam.comment.dto.CommentResponse;

public interface CommentService {
	//게시글에 해당하는 댓글 전체 조회
	public List<CommentResponse> getAllComment(int boardId);
	
	//댓글 저장
	public CommentResponse save(CommentRequest request);
	
	//댓글 수정
	public CommentResponse modify(CommentRequest request);
	
	public void delete(CommentRequest request);
}
