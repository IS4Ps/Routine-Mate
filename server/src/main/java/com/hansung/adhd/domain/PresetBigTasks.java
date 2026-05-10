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

    @Column(name = "day_index")
    private Integer dayIndex;

    @Column(name = "start_time", length = 10)
    private String startTime;

    @Column(name = "end_time", length = 10)
    private String endTime;

    @Column(length = 255)
    private String tags;

    public static PresetBigTasks create(Parents parent, String title, String icon,
                                        Integer orderIndex, String startTime,
                                        String endTime, String tags) {
        PresetBigTasks bigTask = new PresetBigTasks();
        bigTask.parent     = parent;
        bigTask.title      = title;
        bigTask.icon       = icon;
        bigTask.orderIndex = orderIndex;
        bigTask.startTime  = startTime;
        bigTask.endTime    = endTime;
        bigTask.tags       = tags;
        return bigTask;
    }

    public void assignToPreset(RoutinePresets preset) {
        this.preset = preset;
    }

    public void assignToPresetWithDayIndex(RoutinePresets preset, int dayIndex) {
        this.preset    = preset;
        this.dayIndex  = dayIndex;
    }

    public void update(String title, String icon, Integer orderIndex,
                       String startTime, String endTime, String tags) {
        this.title      = title;
        this.icon       = icon;
        this.orderIndex = orderIndex;
        this.startTime  = startTime;
        this.endTime    = endTime;
        this.tags       = tags;
    }
}