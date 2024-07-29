package com.aloa.common.video.feignclient;

import com.aloa.common.video.feignclient.vo.RecalculationResult;
import com.aloa.common.video.handler.ReCalculationFiles;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "aloaStar", url = "${endpoint.url.aloa-star}")
public interface AloaStarFeignClient {
    @PostMapping("/extract/card")
    List<RecalculationResult> recalculateForImage(@RequestBody ReCalculationFiles reCalculationFiles);
}
