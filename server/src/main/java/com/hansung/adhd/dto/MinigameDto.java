package com.hansung.adhd.dto;

import com.hansung.adhd.domain.MinigameLogs;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

public class MinigameDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long    childId;
        
        @NotBlank(message = "게임 타입은 필수입니다.")
        private String  gameType;
        
        @NotNull(message = "점수는 필수입니다.")
        private Integer score;
        
        @NotNull(message = "보상 금액은 필수입니다.")
        private Integer rewardAmount;
    }

    @Getter
    @Builder
    public static class MinigameResponse {
        private Long    logId;
        private String  gameType;
        private Integer score;
        private Integer rewardAmount;

        public static MinigameResponse from(MinigameLogs log) {
            return MinigameResponse.builder()
                    .logId(log.getId())
                    .gameType(log.getGameType())
                    .score(log.getScore())
                    .rewardAmount(log.getRewardAmount())
                    .build();
        }
    }

    // ── Go/No-Go ─────────────────────────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoNoGoStartRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long    childId;
        
        private Integer totalCount;
        private Integer difficulty;

        public Integer getTotalCount() { return totalCount != null ? totalCount : 20; }
        public Integer getDifficulty() { return difficulty != null ? difficulty : 1; }
    }

    @Getter
    @Builder
    public static class Stimulus {
        private Integer index;
        private String  type;
        private String  image;
    }

    @Getter
    @Builder
    public static class GoNoGoStartResponse {
        private String         sessionId;
        private List<Stimulus> stimuli;
        private Integer        timeLimit;
        private Integer        totalCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        private Integer index;
        private Boolean tapped;
        private Integer responseTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoNoGoSubmitRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long         childId;
        
        @NotBlank(message = "세션 ID는 필수입니다.")
        private String       sessionId;
        
        @NotEmpty(message = "답변 목록은 필수입니다.")
        @Valid
        private List<Answer> answers;
    }

    @Getter
    @Builder
    public static class GoNoGoSubmitResponse {
        private Integer correctCount;
        private Integer wrongCount;
        private Integer totalCount;
        private Double  accuracy;
        private Double  avgResponseTime;
        private Integer score;
        private Integer rewardGold;
        private Integer statStrengthGain;
    }

    // ── 스트룹 ────────────────────────────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StroopStartRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long    childId;
        
        private Integer totalCount;
        private Integer difficulty;

        public Integer getTotalCount() { return totalCount != null ? totalCount : 20; }
        public Integer getDifficulty() { return difficulty != null ? difficulty : 1; }
    }

    @Getter
    @Builder
    public static class StroopStimulus {
        private Integer index;
        private String  word;
        private String  inkColor;
        private Boolean isMatch;
    }

    @Getter
    @Builder
    public static class StroopStartResponse {
        private String               sessionId;
        private List<StroopStimulus> stimuli;
        private Integer              timeLimit;
        private Integer              totalCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StroopAnswer {
        private Integer index;
        private String  selectedColor;
        private Integer responseTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StroopSubmitRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long               childId;
        
        @NotBlank(message = "세션 ID는 필수입니다.")
        private String             sessionId;
        
        @NotEmpty(message = "답변 목록은 필수입니다.")
        @Valid
        private List<StroopAnswer> answers;
    }

    @Getter
    @Builder
    public static class StroopSubmitResponse {
        private Integer correctCount;
        private Integer wrongCount;
        private Integer totalCount;
        private Double  accuracy;
        private Double  avgResponseTime;
        private Integer score;
        private Integer rewardGold;
        private Integer statCreativityGain;
    }

    // ── N-Back ────────────────────────────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NBackStartRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long    childId;
        
        private Integer nLevel;      // N 값 (기본 2 → 2-Back)
        private Integer totalCount;  // 자극 수 (기본 20개)

        public Integer getNLevel()     { return nLevel != null ? nLevel : 2; }
        public Integer getTotalCount() { return totalCount != null ? totalCount : 20; }
    }

    /** N-Back 자극 하나 */
    @Getter
    @Builder
    public static class NBackStimulus {
        private Integer index;
        private String  value;   // 자극값 (예: 사과, 바나나 등 이미지 키)
    }

    @Getter
    @Builder
    public static class NBackStartResponse {
        private String              sessionId;
        private Integer             nLevel;       // Flutter가 N값 알아야 함
        private List<NBackStimulus> stimuli;
        private Integer             displayTime;  // 자극 표시 시간 (ms)
        private Integer             intervalTime; // 자극 간 간격 (ms)
        private Integer             totalCount;
    }

    /** N-Back 아이 답변 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NBackAnswer {
        private Integer index;
        private Boolean matched; // N번 전과 같다고 판단했는지
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NBackSubmitRequest {
        @NotNull(message = "아이 ID는 필수입니다.")
        private Long              childId;
        
        @NotBlank(message = "세션 ID는 필수입니다.")
        private String            sessionId;
        
        @NotEmpty(message = "답변 목록은 필수입니다.")
        @Valid
        private List<NBackAnswer> answers;
    }

    @Getter
    @Builder
    public static class NBackSubmitResponse {
        private Integer correctCount;
        private Integer wrongCount;
        private Integer totalCount;
        private Double  accuracy;
        private Integer score;
        private Integer rewardGold;
        private Integer statIntelligenceGain;
        private Integer nLevel;
    }
}
