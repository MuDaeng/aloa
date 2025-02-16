package com.aloa.common.statistics.entity.primarykey;

import com.aloa.common.card.entity.Card;
import com.aloa.common.card.entity.Engrave;
import com.aloa.common.client.entity.ClientVersion;
import lombok.Data;

import java.io.Serializable;

@Data
public class CardCalculationStatisticsPK implements Serializable {
    private Engrave engrave;
    private Card card;
    private ClientVersion version;
    private boolean independentTrials;
}
