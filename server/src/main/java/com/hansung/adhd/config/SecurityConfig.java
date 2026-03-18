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
import com.hansung.adhd.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터 체인을 활성화하겠다는 강력한 선언!
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. REST API이므로 불필요한 기본 보안 기능들 비활성화
                // (화면이 없는 백엔드 서버이므로 폼 로그인, Basic Http 암호화, CSRF 방어막을 끕니다)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 2. ★ JWT의 핵심: "서버는 기억력이 없다(Stateless)" 선언 ★
                // 스프링 시큐리티야, 앞으로 세션(메모리)에 유저 정보 저장하지 마! 토큰으로만 검사할 거야!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 구역별 출입 통제소 (URL 권한 설정)
                .authorizeHttpRequests(auth -> auth
                        // Swagger API 문서와 아이 기기 로그인 주소는 출입증 없이 무사통과!
                        // /swagger-ui.html 을 명시적으로 추가!
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/auth/child/login").permitAll()
                        // ⭐️ /children 주소로 들어오는 요청은 무조건 인증(토큰)이 필요하다고 못 박음!
                        .requestMatchers("/children/**").authenticated()
                        // 그 외의 모든 찔러보기(API 요청)는 무조건 인증(토큰)을 거쳐야 함
                        .anyRequest().authenticated()
                )
                // ⭐️ OAuth2 로그인 세팅 추가!
                .oauth2Login(oauth2 -> oauth2
                        // 유저 정보를 훔쳐올 요원 배치
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        // 성공 시 JWT 토큰 쥐어주고 리다이렉트 시킬 핸들러 배치
                        .successHandler(oAuth2SuccessHandler)
                )

                // 4. 문지기 배치 작전!
                // 우리가 직접 고용한 문지기(JwtFilter)를 스프링의 기본 세관원(UsernamePasswordAuthenticationFilter)보다 먼저 서게 한다.
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}