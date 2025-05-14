package com.yamyam.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer>{

	List<CommentEntity> findAllByBoard_BoardId(int boardId);
}
