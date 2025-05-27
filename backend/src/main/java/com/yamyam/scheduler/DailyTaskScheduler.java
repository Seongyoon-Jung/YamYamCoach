package com.yamyam.scheduler; 

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yamyam.news.service.NaverNewsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class DailyTaskScheduler {

    private final NaverNewsService naverNewsService;
    public DailyTaskScheduler(NaverNewsService naverNewsService) {
        this.naverNewsService = naverNewsService;
    }
    
    private static final String LOG_PATH = "last_run_naver_news_api.txt";

    /** 
     * 매일 07:00, 08:00, …, 22:00 정각에 실행
     */
    @Scheduled(cron = "0 0 7-22 * * *")
    public void scheduledTask() {
        if (hasAlreadyRunThisHour()) {
            return;
        }
        runTask();
    }

    /**
     * 서버 시작 직후: 
     *   – 현재 시간이 07시 이상 22시 이하이고 
     *   – 아직 “이번 시간대”에 한 번도 안 돌았다면
     */
    @PostConstruct
    public void checkOnStartup() {
        int hour = LocalTime.now().getHour();
        if (hour >= 7 && hour <= 22 && !hasAlreadyRunThisHour()) {
            runTask();
        }
    }

    private boolean hasAlreadyRunThisHour() {
        Path path = Paths.get(LOG_PATH);
        if (!Files.exists(path)) return false;

        try {
            String content = Files.readString(path).trim();
            // 파일에 "yyyy-MM-dd-HH" 포맷으로 저장했다고 가정
            LocalDateTime last = LocalDateTime.parse(content,
                DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
            LocalDateTime nowHour = LocalDateTime.now()
                .withMinute(0).withSecond(0).withNano(0);
            return nowHour.equals(last);
        } catch (Exception e) {
            System.err.println("❗ 로그 읽기 실패: " + e.getMessage());
            return false;
        }
    }

    private void updateLastRunDate() {
        String record = LocalDateTime.now()
            .withMinute(0).withSecond(0).withNano(0)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
        try {
            Files.writeString(Paths.get(LOG_PATH), record);
        } catch (IOException e) {
            System.err.println("❗ 로그 쓰기 실패: " + e.getMessage());
        }
    }

    private void runTask() {
        naverNewsService.fetchDailyNaverNews();
        System.out.println("🚀 작업 실행됨: " + LocalDateTime.now());
        updateLastRunDate();
    }
}

