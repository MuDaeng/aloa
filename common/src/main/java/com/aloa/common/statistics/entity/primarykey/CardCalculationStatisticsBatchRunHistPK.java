package com.aloa.common.statistics.entity.primarykey;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class CardCalculationStatisticsBatchRunHistPK implements Serializable {
    private LocalDate date;
    private int runCount;
    private boolean finished;
}
