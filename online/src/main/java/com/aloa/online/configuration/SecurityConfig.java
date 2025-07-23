package com.aloa.online.configuration;

import com.aloa.online.configuration.security.oauth.OAuth2FailureHandler;
import com.aloa.online.configuration.security.oauth.OAuth2SuccessHandler;
import com.aloa.online.configuration.security.oauth.OAuth2UserService;
import com.aloa.online.configuration.security.oauth.jwt.JwtAuthFilter;
import com.aloa.online.configuration.security.oauth.jwt.JwtExceptionFilter;
import com.aloa.common.user.entitiy.primarykey.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
        return web -> web.ignoring()
                // error endpoint를 열어줘야 함, favicon.ico 추가!
                .requestMatchers("http://localhost:3000/**", "/error", "/favicon.ico",
                        "/swagger-ui/**","/swagger-resources/**","/v3/api-docs/**");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(
                        headerConfig -> headerConfig.frameOptions(FrameOptionsConfig::disable).disable()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers("/video/reg", "/video/character", "/card/inquiry/v1/user").hasRole(UserRole.USER.getCode())
                        .requestMatchers("/youtube/v1/youtube", "youtube/v1/reg-video").hasRole(UserRole.USER.getCode())
                        .requestMatchers("/","/oauth/token", "/api-docs/swagger-config", "/api-docs").permitAll()
                        .anyRequest().permitAll()
                )
                // 로그아웃 성공 시 / 주소로 이동
                .logout(
                        logoutConfig -> logoutConfig
//                                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout", "GET"))
                                .logoutSuccessUrl("/")
//                                .invalidateHttpSession(true)
                )
                // OAuth2 로그인 기능에 대한 여러 설정
                .oauth2Login(oauth -> // OAuth2 로그인 기능에 대한 여러 설정의 진입점
                        // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
                        oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
                                // 로그인 성공 시 핸들러
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(oAuth2FailureHandler)
                );

        return http.addFilterBefore(jwtAuthFilter, OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class)
                .build();
//        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Vue.js의 주소를 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
