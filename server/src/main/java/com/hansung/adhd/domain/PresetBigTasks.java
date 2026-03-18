package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Preset_Big_Tasks")
public class PresetBigTasks extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "big_task_id")
    private Long id;

    // 프리셋에 묶이기 전엔 null, 나중에 프리셋에 묶으면 값이 생김
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preset_id", nullable = true)
    private RoutinePresets preset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parents parent;

    @Column(length = 100)
    private String title;

    @Column(length = 50)
    private String icon;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "start_time", length = 10)
    private String startTime;

    @Column(name = "end_time", length = 10)
    private String endTime;

    // ── 정적 팩토리 (프리셋 없이 생성) ───────────────────────────────────────
    public static PresetBigTasks create(Parents parent, String title, String icon,
                                        Integer orderIndex, String startTime, String endTime) {
        PresetBigTasks bigTask = new PresetBigTasks();
        bigTask.parent     = parent;
        bigTask.title      = title;
        bigTask.icon       = icon;
        bigTask.orderIndex = orderIndex;
        bigTask.startTime  = startTime;
        bigTask.endTime    = endTime;
        return bigTask;
    }

    // ── 프리셋에 묶기 ─────────────────────────────────────────────────────────
    public void assignToPreset(RoutinePresets preset) {
        this.preset = preset;
    }

    // ── 수정 ─────────────────────────────────────────────────────────────────
    public void update(String title, String icon, Integer orderIndex,
                       String startTime, String endTime) {
        this.title      = title;
        this.icon       = icon;
        this.orderIndex = orderIndex;
        this.startTime  = startTime;
        this.endTime    = endTime;
    }
}