package com.ureca.snac.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.filter.CustomLogoutFilter;
import com.ureca.snac.auth.filter.JWTFilter;
import com.ureca.snac.auth.filter.LoginFilter;
import com.ureca.snac.auth.oauth2.CustomAuthorizationRequestResolver;
import com.ureca.snac.auth.oauth2.CustomOAuth2FailHandler;
import com.ureca.snac.auth.oauth2.CustomOAuth2SuccessHandler;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.auth.service.CustomOAuth2UserService;
import com.ureca.snac.auth.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customSuccessHandler;
    private final CustomOAuth2FailHandler customFailHandler;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RefreshRepository refreshRepository) throws Exception {

        http.
                cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of(
                            "http://localhost:3000",
                            "http://localhost:5500",
                            "http://127.0.0.1:5500",
                            "http://127.0.0.1:3000",
                            "https://docs.tosspayments.com",
                            "https://snac-app.com",
                            "https://www.snac-app.com",
                            "https://api.snac-app.com",
                            "https://develop.df83wi2m9axuw.amplifyapp.com",
                            "https://seungwoo.i234.me",
                            "https://kapi.kakao.com"

                    ));
                    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of("*"));
                    cfg.setExposedHeaders(List.of("Authorization"));
                    cfg.setAllowCredentials(true);
                    return cfg;
                }));

        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailHandler)
                        .userInfoEndpoint(userinfo -> userinfo
                                .userService(customOAuth2UserService))
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                );


        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());


        http
                .addFilterBefore(new JWTFilter(objectMapper, jwtUtil), OAuth2AuthorizationRequestRedirectFilter.class);
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, objectMapper, refreshRepository), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository, objectMapper), LogoutFilter.class);


        // jwt를 통한 인증/인가를 위해서 세션을 stateless 상태로 설정해야 됨
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
