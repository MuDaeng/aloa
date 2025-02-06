package com.aloa.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Vue.js의 주소에 해당하는 포트 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true); // 필요에 따라 세션 쿠키를 전송할 수 있도록 허용
    }
}
