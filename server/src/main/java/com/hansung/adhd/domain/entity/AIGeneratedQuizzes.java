package com.hansung.adhd.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "AI_Generated_Quizzes")
public class AIGeneratedQuizzes {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    @Column(length = 50)
    private String category;

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "option_json", columnDefinition = "JSON")
    private String optionJson;

    @Column(name = "correct_answer", length = 255)
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "is_solved")
    private Boolean isSolved = false;

    @Column(name = "child_answer", length = 255)
    private String childAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}