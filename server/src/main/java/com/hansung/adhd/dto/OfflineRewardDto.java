package com.hansung.adhd.dto;

import com.hansung.adhd.domain.OfflineRewards;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class OfflineRewardDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long    childId;
        private String  periodType;      // DAILY, WEEKLY 등
        private Integer targetPercent;   // 목표 달성률 (예: 80)
        private String  rewardPromiseText; // 보상 내용 (예: "아이스크림 사주기")
    }

    @Getter
    @Builder
    public static class RewardResponse {
        private Long    rewardId;
        private String  periodType;
        private Integer targetPercent;
        private String  rewardPromiseText;
        private String  status;

        public static RewardResponse from(OfflineRewards reward) {
            return RewardResponse.builder()
                    .rewardId(reward.getId())
                    .periodType(reward.getPeriodType())
                    .targetPercent(reward.getTargetPercent())
                    .rewardPromiseText(reward.getRewardPromiseText())
                    .status(reward.getStatus())
                    .build();
        }
    }
}
