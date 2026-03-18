package com.hansung.adhd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml의 jwt 설정값들을 객체로 매핑해주는 레코드
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessExpiration,
        long refreshExpiration
) {
}