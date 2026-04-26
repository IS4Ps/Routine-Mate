package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Offline_Rewards")
public class OfflineRewards extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    @Column(name = "period_type", length = 20)
    private String periodType;

    @Column(name = "target_days")
    private Integer targetDays;      // 목표 성공 일수 (예: 5일)

    @Column(name = "target_percent")
    private Integer targetPercent;   // 일별 성공 기준 달성률 (기본 70%)

    @Column(name = "reward_promise_text", length = 255)
    private String rewardPromiseText;

    @Column(length = 20)
    private String status;

    public static OfflineRewards create(Children child, String periodType,
                                        Integer targetDays, Integer targetPercent,
                                        String rewardPromiseText) {
        OfflineRewards reward = new OfflineRewards();
        reward.child = child;
        reward.periodType = periodType;
        reward.targetDays = targetDays;
        reward.targetPercent = targetPercent != null ? targetPercent : 70; // 기본 70%
        reward.rewardPromiseText = rewardPromiseText;
        reward.status = "PENDING";
        return reward;
    }

    public void complete() {
        this.status = "COMPLETED";
    }
}