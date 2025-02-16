package com.aloa.common.video.feignclient;

import com.aloa.common.video.feignclient.vo.CharacterProfile;
import com.aloa.common.configuration.LostArkConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "lostArk", url = "${endpoint.url.lost-ark}", configuration = LostArkConfig.class)
public interface LostArkFeignClient {
    @GetMapping("/characters/{characterName}/siblings")
    List<CharacterProfile> retrieveAllCharacter(@PathVariable String characterName);

    @GetMapping("/armories/characters/{characterName}/profiles")
    CharacterProfile retrieveCharacterProfile(@PathVariable String characterName);
}
