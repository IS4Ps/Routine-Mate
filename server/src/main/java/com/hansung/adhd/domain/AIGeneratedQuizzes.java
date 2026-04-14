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

    @Column(name = "correct_answer", length = 255)
    private String correctAnswer;  // "O" or "X"

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "is_solved")
    private Boolean isSolved = false;

    @Column(name = "child_answer", length = 255)
    private String childAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    // ── 정적 팩토리 ──────────────────────────────────────────────────────────
    public static AIGeneratedQuizzes create(Children child, String category,
                                            String questionText, String correctAnswer,
                                            String explanation) {
        AIGeneratedQuizzes quiz = new AIGeneratedQuizzes();
        quiz.child = child;
        quiz.category = category;
        quiz.questionText = questionText;
        quiz.correctAnswer = correctAnswer;
        quiz.explanation = explanation;
        quiz.isSolved = false;
        return quiz;
    }

    // ── 정답 제출 ─────────────────────────────────────────────────────────────
    public void submitAnswer(String childAnswer) {
        this.childAnswer = childAnswer;
        this.isCorrect = this.correctAnswer.equalsIgnoreCase(childAnswer);
        this.isSolved = true;
    }
}