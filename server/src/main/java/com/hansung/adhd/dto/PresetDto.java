package com.hansung.adhd.dto;

import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import com.hansung.adhd.domain.RoutinePresets;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

public class PresetDto {

    /** 프리셋 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long   parentId;
        private String title;
        private String description;
        private String icon;
        private Integer durationDays;
        private List<BigTaskCreateRequest> bigTasks;
    }

    /** BigTask 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BigTaskCreateRequest {
        private String title;
        private String icon;
        private Integer orderIndex;
        private String startTime;
        private List<SmallTaskCreateRequest> smallTasks;
    }

    /** SmallTask 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmallTaskCreateRequest {
        private String title;
        private String tags;
        private String difficultyLevel;
        private Integer orderIndex;
    }

    /** 프리셋 수정 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String description;
        private String icon;
        private Integer durationDays;
    }

    /** 프리셋 목록 응답 */
    @Getter
    @Builder
    public static class PresetResponse {
        private Long   presetId;
        private String title;
        private String description;
        private String icon;
        private Integer durationDays;

        public static PresetResponse from(RoutinePresets preset) {
            return PresetResponse.builder()
                    .presetId(preset.getId())
                    .title(preset.getTitle())
                    .description(preset.getDescription())
                    .icon(preset.getIcon())
                    .durationDays(preset.getDurationDays())
                    .build();
        }
    }

    /** BigTask 응답 */
    @Getter
    @Builder
    public static class BigTaskResponse {
        private Long   bigTaskId;
        private String title;
        private String icon;
        private int    orderIndex;
        private String startTime;
        private List<SmallTaskResponse> smallTasks;

        public static BigTaskResponse from(PresetBigTasks bigTask, List<SmallTaskResponse> smallTasks) {
            return BigTaskResponse.builder()
                    .bigTaskId(bigTask.getId())
                    .title(bigTask.getTitle())
                    .icon(bigTask.getIcon())
                    .orderIndex(bigTask.getOrderIndex())
                    .startTime(bigTask.getStartTime())
                    .smallTasks(smallTasks)
                    .build();
        }
    }

    /** SmallTask 응답 */
    @Getter
    @Builder
    public static class SmallTaskResponse {
        private Long   smallTaskId;
        private String title;
        private String tags;
        private String difficultyLevel;
        private int    orderIndex;

        public static SmallTaskResponse from(PresetSmallTasks smallTask) {
            return SmallTaskResponse.builder()
                    .smallTaskId(smallTask.getId())
                    .title(smallTask.getTitle())
                    .tags(smallTask.getTags())
                    .difficultyLevel(smallTask.getDifficultyLevel())
                    .orderIndex(smallTask.getOrderIndex())
                    .build();
        }
    }
}