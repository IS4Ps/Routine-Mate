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
@Table(name = "Daily_Missions")
public class DailyMissions extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    @Column(name = "origin_preset_id")
    private Long originPresetId;

    @Column(name = "origin_big_task_id")
    private Long originBigTaskId;

    @Column(name = "preset_title", length = 100)
    private String presetTitle;

    @Column(name = "big_task_title", length = 100)
    private String bigTaskTitle;

    @Column(name = "small_task_title", length = 100)
    private String smallTaskTitle;

    @Column(length = 255)
    private String tags;

    @Column(name = "assigned_exp")
    private Integer assignedExp;

    private LocalDate date;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(length = 20)
    private String status;

    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    @Column(name = "start_time", length = 10)
    private String startTime;

    @Column(name = "end_time", length = 10)
    private String endTime;

    public void complete(LocalDateTime completedAt) {
        this.status = "COMPLETED";
        this.completedAt = completedAt;
    }

    public void approve(LocalDateTime approvedAt) {
        this.status = "APPROVED";
        this.approvedAt = approvedAt;
    }

    public void reject(String rejectReason) {
        this.status = "REJECTED";
        this.rejectReason = rejectReason;
    }

    public void start(LocalDateTime startedAt) {
        this.status = "IN_PROGRESS";
        this.startedAt = startedAt;
    }

    public static DailyMissions create(
            Children child,
            Long originPresetId,
            Long originBigTaskId,
            String presetTitle,
            String bigTaskTitle,
            String tags,
            Integer assignedExp,
            LocalDate date,
            String startTime,
            String endTime
    ) {
        DailyMissions mission = new DailyMissions();
        mission.child = child;
        mission.originPresetId = originPresetId;
        mission.originBigTaskId = originBigTaskId;
        mission.presetTitle = presetTitle;
        mission.bigTaskTitle = bigTaskTitle;
        mission.tags = tags;
        mission.assignedExp = assignedExp;
        mission.date = date;
        mission.startTime = startTime;
        mission.endTime = endTime;
        mission.status = "PENDING";
        return mission;
    }
}
