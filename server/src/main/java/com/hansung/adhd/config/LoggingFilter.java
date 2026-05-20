package com.hansung.adhd.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 시큐리티 컨텍스트에서 현재 로그인한 사용자 ID(Subject) 추출
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = (auth != null && auth.isAuthenticated()) ? auth.getName() : "ANONYMOUS";

            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();
            int status = httpResponse.getStatus();

            // [API LOG] [GET] /api/v1/missions (200) - User: 7, Time: 45ms
            log.info("[API LOG] [{}] {} ({}) - User: {}, Time: {}ms",
                    method, uri, status, userId, duration);
        }
    }
}
