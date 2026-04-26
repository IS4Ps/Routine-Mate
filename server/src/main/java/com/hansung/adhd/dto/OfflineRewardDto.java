package com.hansung.adhd.dto;

import com.hansung.adhd.domain.OfflineRewards;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class OfflineRewardDto {

    /** 보상 등록 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long    childId;
        private String  periodType;        // WEEKLY
        private Integer targetDays;        // 목표 성공 일수 (예: 5)
        private Integer targetPercent;     // 일별 성공 기준 달성률 (기본 70%, 생략 가능)
        private String  rewardPromiseText; // 보상 내용 (예: "아이스크림 사주기")
    }

    /** 보상 응답 */
    @Getter
    @Builder
    public static class RewardResponse {
        private Long    rewardId;
        private String  periodType;
        private Integer targetDays;        // 목표 성공 일수
        private Integer targetPercent;     // 일별 성공 기준 달성률
        private String  rewardPromiseText;
        private String  status;

        public static RewardResponse from(OfflineRewards reward) {
            return RewardResponse.builder()
                    .rewardId(reward.getId())
                    .periodType(reward.getPeriodType())
                    .targetDays(reward.getTargetDays())
                    .targetPercent(reward.getTargetPercent())
                    .rewardPromiseText(reward.getRewardPromiseText())
                    .status(reward.getStatus())
                    .build();
        }
    }
}