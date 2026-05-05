package com.hansung.adhd.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequestDto {
    // 프론트(Flutter)가 폰에서 카카오 SDK로 받아온 카카오 Access Token
    private String accessToken;
}