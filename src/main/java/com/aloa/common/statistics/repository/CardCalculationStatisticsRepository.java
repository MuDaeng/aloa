package com.aloa.common.statistics.repository;

import com.aloa.common.client.entity.ClientVersion;
import com.aloa.common.statistics.entity.CardCalculationStatistics;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardCalculationStatisticsRepository extends JpaRepository<CardCalculationStatistics, Integer> {
    List<CardCalculationStatistics> findByVersionAndIndependentTrials(@NonNull ClientVersion version, boolean independentTrials);

    @Query("select i from CardCalculationStatistics i where i.independentTrials = :independentTrials And i.version.updateDate = max(i.version.updateDate) order by i.version.version desc, i.card")
    List<CardCalculationStatistics> findLastCalculation(@Param("independentTrials") boolean independentTrials);
}
