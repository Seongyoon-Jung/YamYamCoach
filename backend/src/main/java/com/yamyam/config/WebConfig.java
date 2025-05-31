package com.yamyam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api 밑으로 들어온 요청은 모두다 받는다
                .allowedOrigins("http://localhost:5173") // 허용할 출처 : 특정 도메인만 받을 수 있음
                .allowedMethods("GET", "POST","PUT","DELETE") // 허용할 HTTP method
                .allowCredentials(true); // 쿠키 인증 요청 허용 이걸 설정해야지만 JSessionID가 포함된 쿠키를 프론트 서버에서 받을수있다.
    }
}
