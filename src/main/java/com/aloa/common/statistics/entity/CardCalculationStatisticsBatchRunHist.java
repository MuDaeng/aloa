package com.aloa.common.statistics.entity;

import com.aloa.common.statistics.entity.primarykey.CardCalculationStatisticsBatchRunHistPK;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CardCalculationStatisticsBatchRunHistPK.class)
@Entity
public class CardCalculationStatisticsBatchRunHist {
    @Id
    @Column(nullable = false)
    private LocalDate date;
    @Id
    private int runCount;
    @Id
    private boolean finished;
}
