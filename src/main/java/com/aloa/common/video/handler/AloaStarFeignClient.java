package com.aloa.common.video.handler;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "aloaStar", url = "http://localhost:8000")
public interface AloaStarFeignClient {
    @PostMapping("/extract/card")
    List<RecalculationResult> recalculateForImage(@RequestBody ReCalculationFiles reCalculationFiles);
}
