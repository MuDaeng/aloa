package com.aloa.online.configuration.security.oauth;

import com.aloa.online.token.AuthTokenProvider;
import com.aloa.online.token.Token;
import com.aloa.online.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final AuthTokenProvider authTokenProvider;
    private final TokenRepository tokenRepository;
    @Value("${endpoint.url.front}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //accessToken 발급
        var accessToken = authTokenProvider.generateAccessToken(authentication);
        var refreshToken = authTokenProvider.generateRefreshToken(authentication);

        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        tokenRepository.save(token);

        //토큰 전달을 위한 redirect
        var redirectUrl = frontUrl + "/oauth2/callback?accessToken=" + accessToken + "&refreshToken=" + refreshToken;
        response.sendRedirect(redirectUrl);
    }
}
