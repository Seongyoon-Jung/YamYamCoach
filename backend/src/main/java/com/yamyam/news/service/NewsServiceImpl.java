package com.yamyam.news.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yamyam.news.dto.NewsResponse;
import com.yamyam.news.entity.NewsEntity;
import com.yamyam.persona.entity.PersonaEntity;
import com.yamyam.news.repository.NewsRepository;
import com.yamyam.persona.repository.PersonaRepository;

@Service
public class NewsServiceImpl implements NewsService {
	private final NewsRepository newsRepository;
	private final PersonaRepository personaRepository;
	public NewsServiceImpl(NewsRepository newsRepository, PersonaRepository personaRepository) {
		this.newsRepository = newsRepository;
		this.personaRepository = personaRepository;
	}

	@Override
	public List<NewsResponse> getAllNews() {
		List<NewsEntity> list = newsRepository.findAllByOrderByDateDesc();
		List<NewsResponse> data = new ArrayList<>();
		
		for (NewsEntity e : list) {
			data.add(new NewsResponse(e.getDate(),e.getTitle(),e.getDescription(),e.getNewsUrl(),e.getImageUrl(),e.getTags()));
		}
		
		return data;
	}

	@Override
	public List<NewsResponse> getAllPersonaNews(int personaId) {
		List<NewsResponse> data = new ArrayList<>();
		if(personaId==-1) {
			List<NewsEntity> list = newsRepository.findAllByOrderByDateDesc();
			for (NewsEntity e : list) {
				data.add(new NewsResponse(e.getDate(),e.getTitle(),e.getDescription(),e.getNewsUrl(),e.getImageUrl(),e.getTags()));
			}
		}
		else {
			PersonaEntity persona = personaRepository.findByPersonaId(personaId);
			String[] diseaseTags = persona.getDiseaseTags().split(","); 
			Map<String,String> map = new HashMap<>();
			map.put("obesity", "비만");
			map.put("diabatees", "당뇨");
			map.put("hypertension", "고혈압");
			map.put("hyperlipidemia", "고지혈증");
			
			List<NewsEntity> list = newsRepository.findAllByTagsOrderByDateDesc("건강");
			
			for (String tag : diseaseTags) {
				list.addAll(newsRepository.findAllByTagsOrderByDateDesc(map.get(tag)));
			}
			
			// 최신순을 다시 정렬
			list.sort(Comparator.comparing(NewsEntity::getDate).reversed());
			
			for (NewsEntity e : list) {
				data.add(new NewsResponse(e.getDate(),e.getTitle(),e.getDescription(),e.getNewsUrl(),e.getImageUrl(),e.getTags()));
			}
		}
		
		return data;
	}
	


}
