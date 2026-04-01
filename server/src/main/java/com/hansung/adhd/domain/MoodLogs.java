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

    public static MoodLogs create(Children child, LocalDate date,
                                  String primaryEmotion, String secondaryEmotion, Integer score) {
        MoodLogs log = new MoodLogs();
        log.child = child;
        log.date = date;
        log.primaryEmotion = primaryEmotion;
        log.secondaryEmotion = secondaryEmotion;
        log.score = score;
        return log;
    }

    public void record(String primaryEmotion, String secondaryEmotion, Integer score) {
        this.primaryEmotion = primaryEmotion;
        this.secondaryEmotion = secondaryEmotion;
        this.score = score;
    }

}