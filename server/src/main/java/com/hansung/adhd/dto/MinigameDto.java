package com.hansung.adhd.dto;

import com.hansung.adhd.domain.MinigameLogs;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class MinigameDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long    childId;
        private String  gameType;
        private Integer score;
        private Integer rewardAmount;
    }

    @Getter
    @Builder
    public static class MinigameResponse {
        private Long    logId;
        private String  gameType;
        private Integer score;
        private Integer rewardAmount;

        public static MinigameResponse from(MinigameLogs log) {
            return MinigameResponse.builder()
                    .logId(log.getId())
                    .gameType(log.getGameType())
                    .score(log.getScore())
                    .rewardAmount(log.getRewardAmount())
                    .build();
        }
    }
}
