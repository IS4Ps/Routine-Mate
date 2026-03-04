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

    @Column(name = "target_percent")
    private Integer targetPercent;

    @Column(name = "reward_promise_text", length = 255)
    private String rewardPromiseText;

    @Column(length = 20)
    private String status;



}