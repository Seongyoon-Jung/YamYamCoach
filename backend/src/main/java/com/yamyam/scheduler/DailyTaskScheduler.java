package com.yamyam.scheduler; 

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yamyam.service.NaverNewsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DailyTaskScheduler {

	private final NaverNewsService naverNewsService;
	public DailyTaskScheduler(NaverNewsService naverNewsService) {
		this.naverNewsService = naverNewsService;
	}
	
    private static final String LOG_PATH = "last_run_naver_news_api.txt"; // 프로젝트 루트에 생성됨

    // 매일 오전 3시 실행
    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledTask() {
        if (hasAlreadyRunToday()) {
            return;
        }
        runTask();
    }

    // 서버 시작 시 오전 3시가 이미 지났으면 실행 (단, 오늘 한 번도 안 했다면)
    @PostConstruct
    public void checkOnStartup() {
        if (LocalTime.now().isAfter(LocalTime.of(3, 0)) && !hasAlreadyRunToday()) {
            runTask();
        }
    }

    private boolean hasAlreadyRunToday() {
        Path path = Paths.get(LOG_PATH);
        if (!Files.exists(path)) return false;

        try {
            String content = Files.readString(path).trim();
            LocalDate lastRunDate = LocalDate.parse(content);
            return LocalDate.now().equals(lastRunDate);
        } catch (Exception e) {
            System.err.println("❗ last_run_naver_news_api.txt 읽기 실패: " + e.getMessage());
            return false;
        }
    }

    private void updateLastRunDate() {
        try {
            Files.writeString(Paths.get(LOG_PATH), LocalDate.now().toString());
        } catch (IOException e) {
            System.err.println("❗ last_run_naver_news_api.txt 쓰기 실패: " + e.getMessage());
        }
    }

    private void runTask() {
        // 👇 실제 작업 실행
    	
    	naverNewsService.fetchDailyNaverNews();
        System.out.println("🚀 작업 실행됨: " + LocalDate.now());

        // 날짜 기록
        updateLastRunDate();
    }
}
