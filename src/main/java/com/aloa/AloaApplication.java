package com.aloa;

import com.aloa.common.token.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JwtProperties.class})
@SpringBootApplication
public class AloaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AloaApplication.class, args);
    }

}
