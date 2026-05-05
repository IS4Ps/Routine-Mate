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

// 더 이상 사용하지 않는 클래스 임포트 제거 (오류 방지)
// import com.hansung.adhd.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터 체인을 활성화하겠다는 강력한 선언!
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

                // 2. ★ JWT의 핵심: "서버는 기억력이 없다(Stateless)" 선언 ★
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 구역별 출입 통제소 (URL 권한 설정)
                .authorizeHttpRequests(auth -> auth
                        // Swagger API 문서와 아이 기기 로그인 주소는 출입증 없이 무사통과!
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/auth/child/login").permitAll()

                        // ⭐️ 신규 추가: 프론트엔드가 폰에서 발급받은 구글 토큰을 던져줄 새 API 주소 오픈!
                        //.requestMatchers("/auth/google").permitAll()
                        // ⭐️ 카카오도 무사통과 시키도록 수정!
                        .requestMatchers("/auth/google", "/auth/kakao").permitAll()

                        // ⭐️ /children 주소로 들어오는 요청은 무조건 인증(토큰)이 필요하다고 못 박음!
                        .requestMatchers("/children/**").authenticated()
                        // 이 아래는 연동하면서 토큰 발급 안 받기 위함
                        .requestMatchers("/children/**").permitAll()
                        .requestMatchers("/parents/**").permitAll()
                        .requestMatchers("/api/**").permitAll()

                        // 그 외의 모든 찔러보기(API 요청)는 무조건 인증(토큰)을 거쳐야 함
                        .anyRequest().authenticated()
                )

                // ⭐️ 철거: OAuth2 로그인 세팅 완전 차단! (더 이상 웹 뷰 기반 리다이렉트 안 함)
                /*
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                */

                // 4. 문지기 배치 작전!
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}