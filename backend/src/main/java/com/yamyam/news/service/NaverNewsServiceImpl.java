package com.yamyam.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yamyam.news.entity.NewsEntity;
import com.yamyam.news.repository.NewsRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String[] query = {
        		"건강, 건강관리, 면역력강화, 영양관리, 운동법, 스트레스관리, 수면관리, 정신건강",
        		"비만, 체중조절, 다이어트, 비만관리, 체지방감소, 체중감량, 비만예방",
        		"고혈압, 혈압관리, 저염식, 혈관건강, 혈압측정, 고혈압예방, 동맥경화",
        		"고지혈증, 콜레스테롤, 지질이상, LDL, HDL, 중성지방, 지질관리",
        		"당뇨, 혈당관리, 인슐린, 당뇨식단, 당뇨예방, 당뇨병관리, 혈당측정",
        		};
        
        for (String tags : query) {
        	String url = "https://openapi.naver.com/v1/search/news.json?query=" + tags + "&display=50&sort=sim";
        	
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
        				// 주소 맨끝에 섹션 번호를 추출
        				int sid = sidExtractor(link);
        				
        				String description = item.get("description").asText().replaceAll("<[^>]+>", ""); // HTML 태그 제거
        				description = Parser.unescapeEntities(description, true);
        				
        				String pubDate = item.get("pubDate").asText();
        				ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, formatter);
        				LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        				
        				// 크롤링을 위해 네이버 뉴스만 생활/문화, it/과학 탭에서만 가져올수있도록
        				if (link.contains("https://n.news.naver.com") && (sid==103 || sid==105)) {
        					if(!newsRepository.existsByNewsUrl(link)) {
        						newsRepository.save(new NewsEntity(title,description,tags.split(",")[0],link,crawlNewsImg(link),localDateTime));
        					}
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
    
    private int sidExtractor(String url) {
        // 정규표현식으로 sid= 뒤의 숫자만 추출
        Pattern pattern = Pattern.compile("[?&]sid=(\\d+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String sid = matcher.group(1); // 첫 번째 그룹
            return Integer.parseInt(sid);
        } 
    	
    	return 0;
    }
}
