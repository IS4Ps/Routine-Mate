package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Mood_Logs")
public class MoodLogs extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mood_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    private LocalDate date;

    @Column(name = "primary_emotion", length = 50)
    private String primaryEmotion;

    @Column(name = "secondary_emotion", length = 50)
    private String secondaryEmotion;

    private Integer score;



}