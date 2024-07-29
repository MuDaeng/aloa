package com.aloa.configuration;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.aloa")
public class LostArkConfig {
    /**
     * 로스트아크 api 키
     * */
    @Value("${api.key.lost-ark}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("authorization", "bearer " + apiKey);
            template.header("accept", "application/json");
        };
    }
}
