package com.aloa.online.index;

import com.aloa.common.user.entitiy.primarykey.UserRole;
import com.aloa.common.user.repository.UserRepository;
import com.aloa.configuration.oauth.StatusResponseDTO;
import com.aloa.configuration.oauth.provider.AuthTokenProvider;
import com.aloa.configuration.oauth.provider.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class DummyController {
    private final AuthTokenProvider authTokenProvider;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @GetMapping("/oauth/token")
    public StatusResponseDTO oAuth2Token(@RequestHeader(required = false) String googleUserId){
        var user = userRepository.findByGoogleUserId(googleUserId);

        if(user.isPresent()){
            return StatusResponseDTO.success(Jwts.builder()
                    .signWith(new SecretKeySpec(jwtProperties.getSecretKey().getBytes(), SignatureAlgorithm.HS512.getJcaName()))   // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
                    .setSubject(googleUserId)  // JWT 토큰 제목
                    .setIssuer(jwtProperties.getIssuer())  // JWT 토큰 발급자
                    .setExpiration(Date.from(Instant.now().plus(jwtProperties.getAccessExpirationMinutes(), ChronoUnit.MINUTES)))    // JWT 토큰 만료 시간
                    .claim("role", List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.USER.getCode())))
                    .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))// JWT 토큰 발급 시간
                    .compact());
        }
        return StatusResponseDTO.addStatus(403);
    }

    @GetMapping("/")
    public StatusResponseDTO index(){
        var context = SecurityContextHolder.getContext();
        return StatusResponseDTO.success(context.getAuthentication());
    }
}
