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
import org.springframework.web.cors.CorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailHandler customOAuth2FailHandler;
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
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           RefreshRepository refreshRepository,
                                           CorsConfigurationSource corsConfigurationSource,
                                           AuthenticationManager authenticationManager) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf((auth) -> auth.disable())
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailHandler)
                        .userInfoEndpoint(userinfo -> userinfo
                                .userService(customOAuth2UserService))
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());

        http
                .addFilterBefore(jwtFilter(), OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterAt(loginFilter(authenticationManager, refreshRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository, objectMapper), LogoutFilter.class);

        return http.build();

    }

    private JWTFilter jwtFilter() {
        return new JWTFilter(objectMapper, jwtUtil);
    }

    private LoginFilter loginFilter(AuthenticationManager authenticationManager, RefreshRepository refreshRepository) {
        return new LoginFilter(authenticationManager, jwtUtil, objectMapper, refreshRepository);
    }
}
