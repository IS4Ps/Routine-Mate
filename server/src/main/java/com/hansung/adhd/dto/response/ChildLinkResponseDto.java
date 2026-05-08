package com.hansung.adhd.dto.response;

public record ChildLinkResponseDto(
        String accessToken,
        Long   childId,
        String nickname
) {}
