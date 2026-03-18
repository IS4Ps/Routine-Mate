package com.hansung.adhd.dto.response;

/**
 * 로그인 성공 시 서버가 프론트엔드에게 돌려줄 출입증 바구니
 */
public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}