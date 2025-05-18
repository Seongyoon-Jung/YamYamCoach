package com.yamyam.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yamyam.entity.NewsEntity;
import com.yamyam.repository.NewsRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NaverNewsServiceImpl implements NaverNewsService {

	private final NewsRepository newsRepository;
    public NaverNewsServiceImpl(NewsRepository newsRepository) {
		this.newsRepository = newsRepository;
	}

	@Value("${naver-client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    @Override
    public void fetchDailyNaverNews() {
        String[] query = {"건강", "비만","고혈압","고지혈증","당뇨"};
        
        for (String tags : query) {
        	String url = "https://openapi.naver.com/v1/search/news.json?query=" + tags + "&display=10&sort=sim";
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.set("X-Naver-Client-Id", clientId);
        	headers.set("X-Naver-Client-Secret", clientSecret);
        	
        	HttpEntity<String> entity = new HttpEntity<>("", headers);
        	RestTemplate restTemplate = new RestTemplate();
        	
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        	
        	try {
        		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        		
        		if (response.getStatusCode() == HttpStatus.OK) {
        			ObjectMapper mapper = new ObjectMapper();
        			JsonNode root = mapper.readTree(response.getBody());
        			JsonNode items = root.get("items");
        			
        			
        			for (JsonNode item : items) {
        				String title = item.get("title").asText().replaceAll("<[^>]+>", ""); // HTML 태그 제거
        				title = Parser.unescapeEntities(title, true);
        				
        				String link = item.get("link").asText();
        				
        				String description = item.get("description").asText().replaceAll("<[^>]+>", ""); // HTML 태그 제거
        				description = Parser.unescapeEntities(description, true);
        				
        				String pubDate = item.get("pubDate").asText();
        				ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, formatter);
        				LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        				
        				if (title.contains(tags) && link.contains("https://n.news.naver.com")) {
        					newsRepository.save(new NewsEntity(title,description,tags,link,crawlNewsImg(link),localDateTime));
        				}
        			}
        			
        		} else {
        			System.err.println("❌ API 응답 실패: " + response.getStatusCode());
        		}
        		
        	} catch (Exception e) {
        		System.err.println("❌ 예외 발생: " + e.getMessage());
        	}
		}
    }
    
    private String crawlNewsImg(String link) {
        try {
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .get();

            // id="img1"인 img 태그 하나 선택
            String imageUrl = doc.select("#img1").attr("data-src");

            // 없을 경우 처리
            if (imageUrl == null || imageUrl.isEmpty()) {
                return "";
            }

            return imageUrl;

        } catch (Exception e) {
            return "❌ 크롤링 실패: " + e.getMessage();
        }
    }

}
