package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "AI_Generated_Quizzes")
public class AIGeneratedQuizzes extends BaseEntity {
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

}