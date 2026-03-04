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



}