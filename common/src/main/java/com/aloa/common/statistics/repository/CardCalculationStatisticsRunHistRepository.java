package com.aloa.common.statistics.repository;

import com.aloa.common.statistics.entity.CardCalculationStatisticsRunHist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CardCalculationStatisticsRunHistRepository extends JpaRepository<CardCalculationStatisticsRunHist, Long> {
    Optional<CardCalculationStatisticsRunHist> findFirstByFinishedIsTrueOrderByDateDescRunCountDesc();

    Optional<CardCalculationStatisticsRunHist> findFirstByFinishedIsFalseOrderByDateDescRunCountDesc();

    Optional<CardCalculationStatisticsRunHist> findFirstByDateAndFinishedIsTrueOrderByRunCountDesc(LocalDate date);
}
