package com.aloa.configuration.oauth;

import com.aloa.common.token.TokenRepository;
import com.aloa.common.user.repository.UserRepository;
import com.aloa.configuration.oauth.provider.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final AuthTokenProvider authTokenProvider;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");

        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과)
        if(authHeader == null || authHeader.isBlank()) {
            doFilter(request, response, filterChain);
            return;
        }

        authHeader = authHeader.replace("Bearer ", "").replace("\"", "");

        if(!authTokenProvider.verifyToken(authHeader)){
            var token = tokenRepository.findByAccessToken(authHeader);
            var refreshToken = token.orElseThrow()
                    .getRefreshToken();
            if(!authTokenProvider.verifyToken(refreshToken)){
                throw new IllegalArgumentException("실패");
            }
        }

        var user = userRepository.findByGoogleUserId(authTokenProvider.getUserId(authHeader))
                .orElseThrow(() -> new IllegalArgumentException("인증토큰이 이상합니다."));

        var oAuth2UserInfo = OAuth2UserMapper.INSTANCE.toOAuth2UserInfo(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(oAuth2UserInfo, "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
