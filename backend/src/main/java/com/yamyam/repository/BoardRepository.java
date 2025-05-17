package com.yamyam.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.entity.BoardEntity;
import com.yamyam.entity.CommentEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer>{

	List<BoardEntity> findAllByOrderByCreatedAtDesc();
	
	BoardEntity findByBoardId(int boardId);
}
