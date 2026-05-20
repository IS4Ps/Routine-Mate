package com.hansung.adhd.config;

import com.hansung.adhd.dto.response.TokenResponseDto;
import com.hansung.adhd.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 로컬 테스트 전용 보안 설정 (spring.profiles.active=local 일 때만 활성화)
 * - 모든 API 토큰 없이 접근 가능
 * - GET /auth/dev-token?id={id}&role={ROLE_PARENT|ROLE_CHILD} 로 JWT 발급 가능
 */
@Profile("local")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class LocalSecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(localCorsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource localCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Profile("local")
    @RestController
    @RequestMapping("/auth")
    @RequiredArgsConstructor
    public static class DevTokenController {

        private final JwtProvider jwtProvider;

        @GetMapping("/dev-token")
        public ApiResponse<TokenResponseDto> devToken(
                @RequestParam Long id,
                @RequestParam(defaultValue = "ROLE_PARENT") String role) {
            String token = jwtProvider.createAccessToken(id, role);
            return ApiResponse.ok(new TokenResponseDto(token, "dev-refresh-token"));
        }
    }
}
