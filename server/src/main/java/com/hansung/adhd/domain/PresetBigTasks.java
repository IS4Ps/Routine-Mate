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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preset_id")
    private RoutinePresets preset;

    @Column(length = 100)
    private String title;

    @Column(length = 50)
    private String icon;

    @Column(name = "order_index")
    private Integer orderIndex;


    // 이 할일을 시작할 시간 (예: "09:00")
    @Column(name = "start_time", length = 10)
    private String startTime;

    // ── 정적 팩토리 ──────────────────────────────────────────────────────────
    public static PresetBigTasks create(RoutinePresets preset, String title,
                                        String icon, Integer orderIndex,String startTime) {
        PresetBigTasks bigTask = new PresetBigTasks();
        bigTask.preset     = preset;
        bigTask.title      = title;
        bigTask.icon       = icon;
        bigTask.orderIndex = orderIndex;
        bigTask.startTime  = startTime;
        return bigTask;
    }

    // ── 수정 메서드 ───────────────────────────────────────────────────────────
    public void update(String title, String icon, Integer orderIndex,
                       Integer dayOffset, String startTime) {
        this.title      = title;
        this.icon       = icon;
        this.orderIndex = orderIndex;
        this.startTime  = startTime;
    }
}