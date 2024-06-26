package com.aloa.common.video.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class YoutubeFeignClientTest {

    @Autowired
    private YoutubeFeignClient youtubeFeignClient;

    @Test
    @DisplayName("유튜브API 사용해보는것")
    void youtube(){
        var s = youtubeFeignClient.getYoutubeInfo("snippet", "path", "zN9Jtk8mIOs");
        for(var key : s.keySet()){
            System.out.print(key + " : " + s.get(key));
        }
    }
}