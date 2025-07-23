package com.aloa.online.statistics.controller;

import com.aloa.common.statistics.entity.CardCalculationStatistics;
import com.aloa.online.statistics.service.StatisticsInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/statistics")
@RequiredArgsConstructor
public class StatisticsInquiryController {
    private final StatisticsInquiryService statisticsInquiryService;

    @GetMapping("/date")
    public List<List<CardCalculationStatistics>> searchStatisticsByDate(String date) {
        return statisticsInquiryService.searchStatisticsByDate(date);
    }
}
