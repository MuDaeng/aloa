package com.aloa.common.client.manager;

import com.aloa.common.client.entity.ClientVersion;
import com.aloa.common.client.repository.ClientVersionRepository;
import com.google.api.client.util.DateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientVersionManager {
    private final ClientVersionRepository clientVersionRepository;

    public ClientVersion getClientVersion(DateTime dateTime) {
        var timeRfc3339String = dateTime.toStringRfc3339().replace("Z", "");
        LocalDateTime localDateTime = LocalDateTime.parse(timeRfc3339String);
        LocalDate publishedAt = localDateTime.toLocalDate();
        Optional<ClientVersion> clientVersion = clientVersionRepository.findFirstByUpdateDateLessThanEqual(publishedAt);
        return clientVersion.orElse(null);
    }
}
