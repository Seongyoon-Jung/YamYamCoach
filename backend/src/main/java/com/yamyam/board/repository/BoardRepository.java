package com.yamyam.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.board.entity.BoardEntity;
import com.yamyam.comment.entity.CommentEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer>{

	List<BoardEntity> findAllByOrderByCreatedAtDesc();
	
	BoardEntity findByBoardId(int boardId);
}
