package com.aloa.common.token;

import com.aloa.common.annotation.Provider;
import com.aloa.common.security.oauth.OAuth2UserInfo;
import com.aloa.common.security.PrincipalDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtException;

import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Provider
@RequiredArgsConstructor
public class AuthTokenProvider {
    private final JwtProperties jwtProperties;
    private final TokenService tokenService;

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getAccessExpirationMinutes(), jwtProperties.getSecretKey());
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getRefreshExpirationMinutes(), jwtProperties.getRefreshSecretKey());
    }

    private String generateToken(Authentication authentication, long expireTime, String secretKey) {

        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Jwts.builder()
                .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))   // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
                .setSubject(authentication.getName())  // JWT 토큰 제목
                .setIssuer(jwtProperties.getIssuer())  // JWT 토큰 발급자
                .setExpiration(Date.from(Instant.now().plus(expireTime, ChronoUnit.MINUTES)))    // JWT 토큰 만료 시간
                .claim("role", authorities)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))// JWT 토큰 발급 시간
                .compact();
    }

    public boolean verifyToken(String token) {
        if (token == null || token.isBlank()) return false;

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecretKey().getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (UnsupportedJwtException
                 | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
    }

    // 3. accessToken 재발급
    public String reissueAccessToken(String accessToken) {
        Optional<Token> optionalToken = tokenService.findByAccessToken(accessToken);

        String refreshToken = optionalToken.map(Token::getRefreshToken).orElse(null);

        if(refreshToken == null || refreshToken.isBlank()) return null;

        if (!verifyToken(refreshToken)) return null;

        Token token = optionalToken.get();

        String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));

        token.setAccessToken(reissueAccessToken);

        tokenService.reissueToken(token);

        return reissueAccessToken;
    }

    private Authentication getAuthentication(String refreshToken) {
        var principal = new PrincipalDetails(tokenToOAuth2UserInfo(refreshToken), null, null);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    public OAuth2UserInfo tokenToOAuth2UserInfo(String token) {
        Claims claims = parseClaims(token);

        return OAuth2UserInfo.builder()
                .name(claims.get("name", String.class))
                .email(claims.getSubject())
                .build();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey().getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new JwtException("INVALID_JWT");
        } catch (SecurityException e) {
            throw new JwtException("INVALID_JWT_SIGNATURE");
        }
    }
}
