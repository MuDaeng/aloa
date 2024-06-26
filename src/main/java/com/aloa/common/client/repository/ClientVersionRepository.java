package com.aloa.common.client.repository;

import com.aloa.common.client.entity.ClientVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ClientVersionRepository extends JpaRepository<ClientVersion, Long> {
    Optional<ClientVersion> findByVersion(String version);
    List<ClientVersion> findByOfficialBalancePatchIsTrue();
    List<ClientVersion> findByVersionIn(List<String> versionList);

    Optional<ClientVersion> findFirstByUpdateDateLessThanEqual(LocalDate updateDate);
}
