package com.aloa.common.security.oauth.jwt;

import com.aloa.common.token.TokenRepository;
import com.aloa.common.user.entitiy.primarykey.UserRole;
import com.aloa.common.user.repository.UserRepository;
import com.aloa.common.token.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
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
            authHeader = authTokenProvider.reissueAccessToken(authHeader);

            if(authHeader == null || authHeader.isBlank()) {
                throw new JwtException("Invalid token");
            }
        }

        var oAuth2UserInfo = authTokenProvider.tokenToOAuth2UserInfo(authHeader);

        Authentication authentication = new UsernamePasswordAuthenticationToken(oAuth2UserInfo, "",
                List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.USER.getCode())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
