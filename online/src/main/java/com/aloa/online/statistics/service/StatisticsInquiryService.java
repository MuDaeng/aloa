package com.aloa.online.statistics.service;

import com.aloa.common.statistics.entity.CardCalculationStatistics;
import com.aloa.common.statistics.entity.CardCalculationStatisticsRunHist;
import com.aloa.common.statistics.repository.CardCalculationStatisticsRunHistRepository;
import com.aloa.common.statistics.repository.CardCalculationStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsInquiryService {

    private final CardCalculationStatisticsRepository statisticsRepository;
    private final CardCalculationStatisticsRunHistRepository statisticsRunHistRepository;

    public List<List<CardCalculationStatistics>> searchStatisticsByDate(final String date) {
        final LocalDate localDate;

        try{
            localDate = LocalDate.parse(date);
        }catch (DateTimeParseException dateTimeParseException){
            throw new IllegalArgumentException("날짜형식에 맞지 않습니다.");
        }

        Optional<CardCalculationStatisticsRunHist> optionalStatisticsRunHist =  statisticsRunHistRepository.findFirstByDateAndFinishedIsTrueOrderByRunCountDesc(localDate);

        if(optionalStatisticsRunHist.isEmpty()) {
            return Collections.emptyList();
        }

        CardCalculationStatisticsRunHist cardCalculationStatisticsRunHist = optionalStatisticsRunHist.get();

        List<CardCalculationStatistics> cardCalculationStatisticsList = statisticsRepository.findByDateAndRunCount(cardCalculationStatisticsRunHist.getDate(), cardCalculationStatisticsRunHist.getRunCount());

        return cardCalculationStatisticsList.stream()
                .collect(Collectors.groupingBy(CardCalculationStatistics::isIndependentTrials))
                .values()
                .stream()
                .toList();
    }
}
