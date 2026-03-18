package com.hansung.adhd.dto;

import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

public class BigTaskDto {

    /** BigTask 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long    parentId;
        private String  title;
        private String  icon;
        private Integer orderIndex;
        private String  startTime;
        private String  endTime;
        private List<SmallTaskCreateRequest> smallTasks;
    }

    /** SmallTask 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmallTaskCreateRequest {
        private String  title;
        private String  tags;
        private String  difficultyLevel;
        private Integer orderIndex;
    }

    /** BigTask 수정 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String  title;
        private String  icon;
        private Integer orderIndex;
        private String  startTime;
        private String  endTime;
    }

    /** SmallTask 응답 */
    @Getter
    @Builder
    public static class SmallTaskResponse {
        private Long    smallTaskId;
        private String  title;
        private String  tags;
        private String  difficultyLevel;
        private Integer orderIndex;

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

    /** BigTask 응답 (SmallTask 포함) */
    @Getter
    @Builder
    public static class BigTaskResponse {
        private Long    bigTaskId;
        private String  title;
        private String  icon;
        private Integer orderIndex;
        private String  startTime;
        private String  endTime;
        private Long    presetId; // null이면 프리셋에 안 묶인 상태
        private List<SmallTaskResponse> smallTasks;

        public static BigTaskResponse from(PresetBigTasks bigTask, List<SmallTaskResponse> smallTasks) {
            return BigTaskResponse.builder()
                    .bigTaskId(bigTask.getId())
                    .title(bigTask.getTitle())
                    .icon(bigTask.getIcon())
                    .orderIndex(bigTask.getOrderIndex())
                    .startTime(bigTask.getStartTime())
                    .endTime(bigTask.getEndTime())
                    .presetId(bigTask.getPreset() != null ? bigTask.getPreset().getId() : null)
                    .smallTasks(smallTasks)
                    .build();
        }
    }
}