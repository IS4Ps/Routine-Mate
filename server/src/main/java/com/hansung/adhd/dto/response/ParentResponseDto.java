package com.hansung.adhd.dto.response;

public record ParentResponseDto(
        Long parentId,
        String email,
        String provider // "google" 또는 "kakao"
) {
}