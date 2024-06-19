package com.aloa.configuration;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.aloa")
public class OpenFeignConfig {
    private final String apiKey;

    {
        apiKey = "AIzaSyAgrQQ13zrtd0PFE5JXiKnRrDrsiOpjRzU";
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("x-api-key", apiKey);
            template.header("Accept", "application/json");
        };
    }
}
