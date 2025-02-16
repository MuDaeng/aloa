package com.aloa.common.statistics.entity;

import com.aloa.common.card.entity.Card;
import com.aloa.common.card.entity.Engrave;
import com.aloa.common.client.entity.ClientVersion;
import com.aloa.common.statistics.entity.primarykey.CardCalculationStatisticsPK;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CardCalculationStatisticsPK.class)
@Entity
public class CardCalculationStatistics {
    @Enumerated(EnumType.STRING)
    @Id
    private Engrave engrave;
    @Enumerated(EnumType.STRING)
    private Card card;
    private BigInteger cnt;
    @Id
    private boolean independentTrials;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="version")
    @Id
    private ClientVersion version;
}
