package com.hansung.adhd.config;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Profile("!local")  // local 프로파일에서는 LocalSecurityConfig가 대신 동작
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    // 더 이상 스프링 시큐리티가 이 두 녀석을 찾지 않도록 주석 처리!
    // private final CustomOAuth2UserService customOAuth2UserService;
    // private final OAuth2SuccessHandler oAuth2SuccessHandler;

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

                // 4. 구역별 출입 통제소 (URL 권한 설정)
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로들
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/auth/**"
                        ).permitAll()

                        // ⭐️ /children 및 그 하위 경로는 무조건 인증 필요!
                        .requestMatchers("/children", "/children/**").authenticated()
                        .requestMatchers("/parents/**").authenticated()

                        // 그 외의 모든 요청도 인증 필요
                        .anyRequest().authenticated()
                )

                // 5. 문지기 배치 작전!
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