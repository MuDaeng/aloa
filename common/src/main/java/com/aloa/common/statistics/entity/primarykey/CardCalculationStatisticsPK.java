package com.aloa.common.statistics.entity.primarykey;

import com.aloa.common.card.entity.Card;
import com.aloa.common.card.entity.Engrave;
import lombok.Data;

import java.io.Serializable;

@Data
public class CardCalculationStatisticsPK extends CardCalculationStatisticsRunHistPK implements Serializable {
    private Engrave engrave;
    private Card card;
    private boolean independentTrials;
}
