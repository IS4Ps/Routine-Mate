package com.hansung.adhd.dto;

import com.hansung.adhd.domain.AIGeneratedQuizzes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

public class AIQuizDto {

    /** 이미지 하나 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageRequest {
        @NotBlank(message = "이미지 데이터(base64)는 필수입니다.")
        private String imageBase64;
    }

    /** 퀴즈 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long               childId;
        
        @NotBlank(message = "카테고리는 필수입니다.")
        private String             category;    // 예: "수학", "과학"
        
        private List<ImageRequest> images;      // 여러 장

        // 하위 호환 - 1장짜리 기존 방식도 지원
        private String imageBase64;
    }

    /** 정답 제출 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerRequest {
        @NotBlank(message = "정답(O 또는 X)은 필수입니다.")
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
        private String  explanation;

        public static QuizResponse from(AIGeneratedQuizzes quiz) {
            return QuizResponse.builder()
                    .quizId(quiz.getId())
                    .category(quiz.getCategory())
                    .questionText(quiz.getQuestionText())
                    .isSolved(quiz.getIsSolved())
                    .childAnswer(quiz.getChildAnswer())
                    .isCorrect(quiz.getIsCorrect())
                    .explanation(quiz.getIsSolved() ? quiz.getExplanation() : null)
                    .build();
        }
    }

    /** 퀴즈 결과 응답 */
    @Getter
    @Builder
    public static class AnswerResponse {
        private Long    quizId;
        private Boolean isCorrect;
        private String  correctAnswer;
        private String  explanation;
        private Integer rewardGold;
    }

    /** 생성된 퀴즈 목록 응답 */
    @Getter
    @Builder
    public static class GenerateResponse {
        private Integer            totalCount;
        private List<QuizResponse> quizzes;
    }
}
