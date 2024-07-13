package com.aloa;

import com.aloa.common.token.JwtProperties;
import com.aloa.configuration.properties.FFmpegProperties;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JwtProperties.class, FFmpegProperties.class})
@SpringBootApplication
public class AloaApplication {

    public static void main(String[] args) {
        // Load OpenCV native library
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV library: " + e.getMessage());
        }
        SpringApplication.run(AloaApplication.class, args);
    }

}
