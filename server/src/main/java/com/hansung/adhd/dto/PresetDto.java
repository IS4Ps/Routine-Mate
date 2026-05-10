package com.hansung.adhd.dto;

import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import com.hansung.adhd.domain.RoutinePresets;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class PresetDto {

    /** 프리셋 생성 요청 (BigTask 묶음) */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long        parentId;
        private String      title;
        private String      description;
        private String      icon;
        private Integer     durationDays;
        private List<Long>  bigTaskIds;
    }

    /** 프리셋 수정 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String  title;
        private String  description;
        private String  icon;
        private Integer durationDays;
    }

    /** 날짜 기준 프리셋 저장 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveFromDateRequest {
        private Long      parentId;
        private Long      childId;
        private LocalDate date;
        private String    title;
        private String    description;
    }

    /** 프리셋 불러오기 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoadRequest {
        private Long      childId;
        private LocalDate startDate;           // 불러올 시작 날짜
        private Integer   assignedExpPerMission; // 미션당 경험치 (없으면 기본값 20)

        public Integer getAssignedExpPerMission() {
            return assignedExpPerMission != null ? assignedExpPerMission : 20;
        }
    }

    /** 프리셋 목록 응답 */
    @Getter
    @Builder
    public static class PresetResponse {
        private Long    presetId;
        private Long    parentId;
        private String  title;
        private String  description;
        private String  icon;
        private Integer durationDays;

        public static PresetResponse from(RoutinePresets preset) {
            return PresetResponse.builder()
                    .presetId(preset.getId())
                    .parentId(preset.getParent() != null ? preset.getParent().getId() : null)
                    .title(preset.getTitle())
                    .description(preset.getDescription())
                    .icon(preset.getIcon())
                    .durationDays(preset.getDurationDays())
                    .build();
        }
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
        private List<SmallTaskResponse> smallTasks;

        public static BigTaskResponse from(PresetBigTasks bigTask, List<SmallTaskResponse> smallTasks) {
            return BigTaskResponse.builder()
                    .bigTaskId(bigTask.getId())
                    .title(bigTask.getTitle())
                    .icon(bigTask.getIcon())
                    .orderIndex(bigTask.getOrderIndex())
                    .startTime(bigTask.getStartTime())
                    .endTime(bigTask.getEndTime())
                    .smallTasks(smallTasks)
                    .build();
        }
    }
}
