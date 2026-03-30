package com.hansung.adhd.dto.response;

public record ChildResponseDto(
        Long childId,
        String nickname,
        Integer level,
        Integer currentExp,
        Integer gold,
        Integer statStrength,
        Integer statIntelligence,
        Integer statCreativity
) {
}