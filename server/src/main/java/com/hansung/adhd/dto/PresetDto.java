package com.hansung.adhd.dto;

import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import com.hansung.adhd.domain.RoutinePresets;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PresetDto {

    @Getter
    @Builder
    public static class PresetResponse {
        private Long   presetId;
        private String title;
        private String description;
        private String icon;

        public static PresetResponse from(RoutinePresets preset) {
            return PresetResponse.builder()
                    .presetId(preset.getId())
                    .title(preset.getTitle())
                    .description(preset.getDescription())
                    .icon(preset.getIcon())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BigTaskResponse {
        private Long   bigTaskId;
        private String title;
        private String icon;
        private int    orderIndex;
        private List<SmallTaskResponse> smallTasks;

        public static BigTaskResponse from(PresetBigTasks bigTask, List<SmallTaskResponse> smallTasks) {
            return BigTaskResponse.builder()
                    .bigTaskId(bigTask.getId())
                    .title(bigTask.getTitle())
                    .icon(bigTask.getIcon())
                    .orderIndex(bigTask.getOrderIndex())
                    .smallTasks(smallTasks)
                    .build();
        }
    }

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
