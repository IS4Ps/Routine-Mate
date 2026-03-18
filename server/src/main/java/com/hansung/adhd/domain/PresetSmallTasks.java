package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Preset_Small_Tasks")
public class PresetSmallTasks extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "small_task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "big_task_id")
    private PresetBigTasks bigTask;

    @Column(length = 100)
    private String title;

    @Column(length = 255)
    private String tags;

    @Column(name = "difficulty_level", length = 10)
    private String difficultyLevel;

    @Column(name = "order_index")
    private Integer orderIndex;

    public static PresetSmallTasks create(PresetBigTasks bigTask, String title,
                                          String tags, String difficultyLevel, Integer orderIndex) {
        PresetSmallTasks smallTask = new PresetSmallTasks();
        smallTask.bigTask = bigTask;
        smallTask.title = title;
        smallTask.tags = tags;
        smallTask.difficultyLevel = difficultyLevel;
        smallTask.orderIndex = orderIndex;
        return smallTask;
    }

}