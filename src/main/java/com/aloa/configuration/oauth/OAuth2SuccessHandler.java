package com.aloa.configuration.oauth;

import com.aloa.common.token.Token;
import com.aloa.common.token.TokenRepository;
import com.aloa.configuration.oauth.provider.AuthTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final AuthTokenProvider authTokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //accessToken 발급
        var accessToken = authTokenProvider.generateAccessToken(authentication);
        var refreshToken = authTokenProvider.generateRefreshToken(authentication);

        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        tokenRepository.save(token);

        //토큰 전달을 위한 redirect
        String URI = "/";
        var redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .build().toString();

        response.setHeader("accessToken", accessToken);
        response.sendRedirect(redirectUrl);
    }
}
