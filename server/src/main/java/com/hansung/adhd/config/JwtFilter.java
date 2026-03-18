package com.hansung.adhd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    // HTTP 요청이 올 때마다 무조건 이 메서드가 실행됨!
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰을 쏙 빼온다.
        String token = resolveToken(request);

        // 2. 토큰이 비어있지 않고, 조폐국(JwtProvider) 검사를 무사히 통과했다면?
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            // 3. 조폐국에서 유저 정보(Authentication 신분증)를 발급받아
            Authentication authentication = jwtProvider.getAuthentication(token);

            // 4. 시큐리티의 핵심! VIP 명부(SecurityContext)에 신분증을 떡하니 등록한다!
            // 이제 이 요청이 끝날 때까지 스프링은 "아~ 이 유저가 요청한 거구나" 하고 알게 됨.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다", authentication.getName());
        }

        // 5. ★★★ 가장 중요 ★★★ 문지기 역할 끝났으니 다음 필터나 진짜 목적지(Controller)로 넘겨준다!
        filterChain.doFilter(request, response);
    }

    // 헤더에서 "Bearer {토큰}"을 찾아내서 "Bearer " 글자는 떼어버리고 순수 토큰만 반환하는 편의 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "가 7글자니까 그 뒤부터 자름
        }
        return null; // 토큰이 없거나 형식이 틀리면 과감히 null 반환
    }
}