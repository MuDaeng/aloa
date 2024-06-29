package com.aloa.configuration.oauth.provider;

import com.aloa.common.annotation.Provider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Provider
@RequiredArgsConstructor
public class AuthTokenProvider {
    private final JwtProperties jwtProperties;

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getAccessTokenExpirationMinutes());
    }

    public void generateRefreshToken(Authentication authentication) {
        String refreshToken = generateToken(authentication, jwtProperties.getRefreshExpirationMinutes());
    }

    private String generateToken(Authentication authentication, long expireTime) {

        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Jwts.builder()
                .signWith(new SecretKeySpec(jwtProperties.getSecretKey().getBytes(), SignatureAlgorithm.HS512.getJcaName()))   // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
                .setSubject(authentication.getName())  // JWT 토큰 제목
                .setIssuer(jwtProperties.getIssuer())  // JWT 토큰 발급자
                .setExpiration(Date.from(Instant.now().plus(expireTime, ChronoUnit.MINUTES)))    // JWT 토큰 만료 시간
                .claim("role", authorities)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))// JWT 토큰 발급 시간
                .compact();
    }

    public boolean verifyToken(String token) {
        if(token == null || token.isBlank()) return false;

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecretKey().getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .after(new Date());
        }catch(ExpiredJwtException | UnsupportedJwtException
        | MalformedJwtException | SignatureException | IllegalArgumentException e){
            return false;
        }
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return List.of(new SimpleGrantedAuthority(
                claims.get("role").toString()));
    }

    // 3. accessToken 재발급
//    public String reissueAccessToken(String accessToken) {
//        if (StringUtils.hasText(accessToken)) {
//            Token token = tokenService.findByAccessTokenOrThrow(accessToken);
//            String refreshToken = token.getRefreshToken();
//
//            if (validateToken(refreshToken)) {
//                String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
//                tokenService.updateToken(reissueAccessToken, token);
//                return reissueAccessToken;
//            }
//        }
//        return null;
//    }

//    public boolean validateToken(String token) {
//        if (!StringUtils.hasText(token)) {
//            return false;
//        }
//
//        Claims claims = parseClaims(token);
//        return claims.getExpiration().after(new Date());
//    }

//    private Claims parseClaims(String token) {
//        try {
//            return Jwts.parser().verifyWith(secretKey).build()
//                    .parseSignedClaims(token).getPayload();
//        } catch (ExpiredJwtException e) {
//            return e.getClaims();
//        } catch (MalformedJwtException e) {
//            throw new TokenException(INVALID_TOKEN);
//        } catch (SecurityException e) {
//            throw new TokenException(INVALID_JWT_SIGNATURE);
//        }
//    }
}
