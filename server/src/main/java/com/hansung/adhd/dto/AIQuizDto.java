package com.hansung.adhd.dto;

import com.hansung.adhd.domain.AIGeneratedQuizzes;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

public class AIQuizDto {

    /** 퀴즈 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateRequest {
        private Long   childId;
        private String category;   // 예: "수학", "국어", "과학"
        private String imageBase64; // 학습지 이미지 Base64
        private Integer quizCount; // 생성할 퀴즈 수 (기본 5개)

        public Integer getQuizCount() {
            return quizCount != null ? quizCount : 5;
        }
    }

    /** 정답 제출 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerRequest {
        private String answer; // "O" or "X"
    }

    /** 퀴즈 응답 */
    @Getter
    @Builder
    public static class QuizResponse {
        private Long    quizId;
        private String  category;
        private String  questionText;
        private Boolean isSolved;
        private String  childAnswer;
        private Boolean isCorrect;
        private String  explanation; // 정답 제출 후에만 반환

        public static QuizResponse from(AIGeneratedQuizzes quiz) {
            return QuizResponse.builder()
                    .quizId(quiz.getId())
                    .category(quiz.getCategory())
                    .questionText(quiz.getQuestionText())
                    .isSolved(quiz.getIsSolved())
                    .childAnswer(quiz.getChildAnswer())
                    .isCorrect(quiz.getIsCorrect())
                    // 풀었을 때만 해설 반환
                    .explanation(quiz.getIsSolved() ? quiz.getExplanation() : null)
                    .build();
        }
    }

    /** 퀴즈 결과 응답 (정답 제출 후) */
    @Getter
    @Builder
    public static class AnswerResponse {
        private Long    quizId;
        private Boolean isCorrect;
        private String  correctAnswer;
        private String  explanation;
        private Integer rewardGold; // 정답 시 골드 지급
    }

    /** 생성된 퀴즈 목록 응답 */
    @Getter
    @Builder
    public static class GenerateResponse {
        private Integer            totalCount;
        private List<QuizResponse> quizzes;
    }
}
