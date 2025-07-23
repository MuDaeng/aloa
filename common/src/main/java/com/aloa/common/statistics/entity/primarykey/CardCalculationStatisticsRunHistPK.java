package com.aloa.common.statistics.entity.primarykey;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class CardCalculationStatisticsRunHistPK implements Serializable {
    private LocalDate date;
    private int runCount;
}
