package com.hansung.adhd.dto;

import com.hansung.adhd.domain.MoodLogs;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

public class MoodDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long      childId;
        private LocalDate date;
        private String    primaryEmotion;
        private String    secondaryEmotion;
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
}
