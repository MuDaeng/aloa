package com.aloa.daemon.statistics;

import com.aloa.daemon.constants.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardCalculationStatsDaemon {

    @Scheduled(fixedDelay = 10 * Time.MINUTE)
    public void processStatsCardCalculation() {

        System.out.println("Card calculation stats daemon started");
    }

}
