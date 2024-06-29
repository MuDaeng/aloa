package com.aloa.configuration.oauth.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
public class JwtProperties {
    private final String secretKey;
    private final long refreshExpirationMinutes;
    private final long accessTokenExpirationMinutes;
    private final String issuer;
}
