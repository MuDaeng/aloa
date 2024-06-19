package com.aloa.common.statistics.repository;

import com.aloa.common.statistics.entity.CardCalculationStatisticsBatchRunHist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardCalculationStatisticsBatchRunHistRepository extends JpaRepository<CardCalculationStatisticsBatchRunHist, Long> {
    Optional<CardCalculationStatisticsBatchRunHist> findFirstByFinishedIsTrueOrderByDateDescRunCountDesc();

    Optional<CardCalculationStatisticsBatchRunHist> findFirstByFinishedIsFalseOrderByDateDescRunCountDesc();
}
