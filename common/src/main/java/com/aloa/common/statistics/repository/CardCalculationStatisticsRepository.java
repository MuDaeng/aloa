package com.aloa.common.statistics.repository;

import com.aloa.common.statistics.entity.CardCalculationStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CardCalculationStatisticsRepository extends JpaRepository<CardCalculationStatistics, Integer> {

    @Query("select i from CardCalculationStatistics i where i.independentTrials = :independentTrials order by i.card")
    List<CardCalculationStatistics> findLastCalculation(@Param("independentTrials") boolean independentTrials);

    List<CardCalculationStatistics> findLastCalculationByIndependentTrials(boolean independentTrials);

    List<CardCalculationStatistics> findByDateAndRunCount(LocalDate date, int runCount);

}
