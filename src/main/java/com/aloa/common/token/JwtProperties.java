package com.aloa.common.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
public class JwtProperties {
    private final String secretKey;
    private final String refreshSecretKey;
    private final long refreshExpirationMinutes;
    private final long accessExpirationMinutes;
    private final String issuer;
}
