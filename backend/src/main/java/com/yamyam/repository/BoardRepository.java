package com.yamyam.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer>{

	BoardEntity findByBoardId(int boardId);
}
