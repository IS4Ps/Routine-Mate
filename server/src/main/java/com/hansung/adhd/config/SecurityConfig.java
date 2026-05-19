package com.hansung.adhd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// 더 이상 사용하지 않는 클래스 임포트 제거 (오류 방지)
// import com.hansung.adhd.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터 체인을 활성화하겠다는 강력한 선언!
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. REST API이므로 불필요한 기본 보안 기능들 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 2. CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. ★ JWT의 핵심: "서버는 기억력이 없다(Stateless)" 선언 ★
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 예외 처리 핸들러 등록 (401, 403 에러 커스텀)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // 5. 구역별 출입 통제소 (URL 권한 설정)
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로들
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/auth/**",
                                "/error" // ⭐️ 에러 발생 시 내부 리다이렉트 허용 (403 방지)
                        ).permitAll()

                        // ⭐️ /api/ 하위의 모든 API와 /children, /parents 경로 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/children/**", "/parents/**").authenticated()

                        // 그 외의 모든 요청도 인증 필요
                        .anyRequest().authenticated()
                )

                // 6. 문지기 배치 작전!
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*"); // 모든 Origin 허용 (브라우저용)
        configuration.addAllowedMethod("*");        // GET, POST, PUT, DELETE 등 모두 허용
        configuration.addAllowedHeader("*");        // 모든 Header 허용
        configuration.setAllowCredentials(true);    // 자격 증명 허용 (쿠키 등)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}