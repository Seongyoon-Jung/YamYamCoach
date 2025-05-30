package com.yamyam.news.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yamyam.news.entity.NewsEntity;

public interface NewsRepository extends JpaRepository<NewsEntity, Integer> {
	List<NewsEntity> findAllByOrderByDateDesc();
	
	List<NewsEntity> findAllByTagsOrderByDateDesc(String tags);
	
	boolean existsByNewsUrl(String newsUrl);
}
