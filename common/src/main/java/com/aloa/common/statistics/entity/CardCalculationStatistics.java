package com.aloa.common.statistics.entity;

import com.aloa.common.card.entity.Card;
import com.aloa.common.card.entity.Engrave;
import com.aloa.common.statistics.entity.primarykey.CardCalculationStatisticsPK;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CardCalculationStatisticsPK.class)
@Entity
public class CardCalculationStatistics {
    @Id
    private LocalDate date;
    @Id
    private int runCount;
    @Enumerated(EnumType.STRING)
    @Id
    private Engrave engrave;
    @Enumerated(EnumType.STRING)
    @Id
    private Card card;
    private BigInteger cnt;
    @Id
    private boolean independentTrials;
}
