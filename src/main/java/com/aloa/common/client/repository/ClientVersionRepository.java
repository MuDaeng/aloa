package com.aloa.common.client.repository;

import com.aloa.common.client.entity.ClientVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientVersionRepository extends JpaRepository<ClientVersion, Long> {
    List<ClientVersion> findByVersion(String version);
    List<ClientVersion> findByIsOfficialBalancePatch(boolean isOfficialBalancePatch);
}
