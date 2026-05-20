package com.hansung.adhd.dto;

import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

public class BigTaskDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "부모 ID는 필수입니다.")
        private Long    parentId;
        
        @NotBlank(message = "제목은 필수입니다.")
        private String  title;
        
        private String  icon;
        
        @NotNull(message = "순서는 필수입니다.")
        private Integer orderIndex;
        
        private String  startTime;
        private String  endTime;
        private String  tags;
        
        @Valid
        private List<SmallTaskCreateRequest> smallTasks;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmallTaskCreateRequest {
        @NotBlank(message = "소과업 제목은 필수입니다.")
        private String  title;
        
        private String  tags;
        private String  difficultyLevel;
        
        @NotNull(message = "순서는 필수입니다.")
        private Integer orderIndex;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "제목은 필수입니다.")
        private String  title;
        
        private String  icon;
        
        @NotNull(message = "순서는 필수입니다.")
        private Integer orderIndex;
        
        private String  startTime;
        private String  endTime;
        private String  tags;
    }

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

    @Getter
    @Builder
    public static class BigTaskResponse {
        private Long    bigTaskId;
        private String  title;
        private String  icon;
        private Integer orderIndex;
        private String  startTime;
        private String  endTime;
        private String  tags;
        private Long    presetId;
        private List<SmallTaskResponse> smallTasks;

        public static BigTaskResponse from(PresetBigTasks bigTask, List<SmallTaskResponse> smallTasks) {
            return BigTaskResponse.builder()
                    .bigTaskId(bigTask.getId())
                    .title(bigTask.getTitle())
                    .icon(bigTask.getIcon())
                    .orderIndex(bigTask.getOrderIndex())
                    .startTime(bigTask.getStartTime())
                    .endTime(bigTask.getEndTime())
                    .tags(bigTask.getTags())
                    .presetId(bigTask.getPreset() != null ? bigTask.getPreset().getId() : null)
                    .smallTasks(smallTasks)
                    .build();
        }
    }
}
