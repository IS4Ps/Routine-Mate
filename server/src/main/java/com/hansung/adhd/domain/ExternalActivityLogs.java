package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "External_Activity_Logs")
public class ExternalActivityLogs extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    private LocalDate date;

    @Column(name = "activity_type", length = 50)
    private String activityType;

    private Integer value;

    @Column(name = "rewarded_exp")
    private Integer rewardedExp;

    @Column(name = "rewarded_stat_type", length = 50)
    private String rewardedStatType;

    @Column(name = "rewarded_stat_amount")
    private Integer rewardedStatAmount;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;



}