package com.hansung.adhd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AiRoutineDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long childId;
    }

    // AI가 JSON으로 응답할 포맷
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String title;
        private String description;
        private int exp; // 예상 보상 경험치
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private List<Recommendation> recommendedRoutines;
    }
}