package com.yamyam.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.comment.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer>{

	List<CommentEntity> findAllByBoard_BoardIdOrderByCreatedAtDesc(int boardId);
	
	CommentEntity findByCommentId(int commentId);
}
