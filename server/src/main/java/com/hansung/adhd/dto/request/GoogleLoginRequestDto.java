package com.hansung.adhd.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginRequestDto {
    // 프론트(Flutter)가 폰에서 직접 받아온 구글 ID Token
    private String idToken;
}