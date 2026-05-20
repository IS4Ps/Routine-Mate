package com.hansung.adhd.dto;

import com.hansung.adhd.domain.MoodLogs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

public class MoodDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long      childId;
        
        @NotNull(message = "날짜는 필수입니다.")
        private LocalDate date;
        
        @NotBlank(message = "주요 감정은 필수입니다.")
        private String    primaryEmotion;
        
        private String    secondaryEmotion;
        
        @NotNull(message = "점수는 필수입니다.")
        private Integer   score;
    }

    @Getter
    @Builder
    public static class MoodResponse {
        private Long      moodId;
        private LocalDate date;
        private String    primaryEmotion;
        private String    secondaryEmotion;
        private Integer   score;

        public static MoodResponse from(MoodLogs log) {
            return MoodResponse.builder()
                    .moodId(log.getId())
                    .date(log.getDate())
                    .primaryEmotion(log.getPrimaryEmotion())
                    .secondaryEmotion(log.getSecondaryEmotion())
                    .score(log.getScore())
                    .build();
        }
    }

    /** 월간 감정 캘린더 응답 */
    @Getter
    @Builder
    public static class MonthlyMoodResponse {
        private Integer year;
        private Integer month;
        private Integer totalCount;   // 해당 월 기록 수
        private Double  avgScore;     // 해당 월 평균 감정 점수
        private Map<String, MoodResponse> moodMap; // "2026-04-05" → MoodResponse
    }
}
