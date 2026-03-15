package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Routine_Presets")
public class RoutinePresets extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preset_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parents parent;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String icon;

    // 프리셋이 몇 일짜리인지 (1일짜리, 3일짜리 등)
    @Column(name = "duration_days")
    private Integer durationDays;

    // ── 정적 팩토리 ──────────────────────────────────────────────────────────
    public static RoutinePresets create(Parents parent, String title,
                                        String description, String icon,
                                        Integer durationDays) {
        RoutinePresets preset = new RoutinePresets();
        preset.parent      = parent;
        preset.title       = title;
        preset.description = description;
        preset.icon        = icon;
        preset.durationDays = durationDays;
        return preset;
    }

    // ── 수정 메서드 ───────────────────────────────────────────────────────────
    public void update(String title, String description, String icon, Integer durationDays) {
        this.title        = title;
        this.description  = description;
        this.icon         = icon;
        this.durationDays = durationDays;
    }
}