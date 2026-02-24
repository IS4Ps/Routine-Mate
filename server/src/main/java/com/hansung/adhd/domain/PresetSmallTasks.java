package com.hansung.adhd.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Preset_Small_Tasks")
public class PresetSmallTasks {
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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}