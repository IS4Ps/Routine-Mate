package com.hansung.adhd.dto;

import com.hansung.adhd.domain.DailyMissions;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MissionDto {

    /** 미션 생성 요청 (부모가 아이한테 미션 할당) */
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private Long   childId;
        private Long   originPresetId;
        private Long   originBigTaskId;
        private String presetTitle;
        private String bigTaskTitle;
        private String smallTaskTitle;
        private String tags;
        private Integer assignedExp;
        private LocalDate date;
    }

    /** 미션 완료 요청 (아이가 완료 버튼 누를 때) */
    @Getter
    public static class CompleteRequest {
        private LocalDateTime completedAt;
    }

    /** 미션 승인/거절 요청 (부모가 검토할 때) */
    @Getter
    public static class ReviewRequest {
        private String status;       // APPROVED or REJECTED
        private String rejectReason; // 거절 시 사유
    }

    /** 미션 응답 */
    @Getter
    @Builder
    public static class MissionResponse {
        private Long          missionId;
        private String        presetTitle;
        private String        bigTaskTitle;
        private String        smallTaskTitle;
        private String        tags;
        private Integer       assignedExp;
        private LocalDate     date;
        private String        status;
        private String        rejectReason;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private LocalDateTime approvedAt;

        public static MissionResponse from(DailyMissions mission) {
            return MissionResponse.builder()
                    .missionId(mission.getId())
                    .presetTitle(mission.getPresetTitle())
                    .bigTaskTitle(mission.getBigTaskTitle())
                    .smallTaskTitle(mission.getSmallTaskTitle())
                    .tags(mission.getTags())
                    .assignedExp(mission.getAssignedExp())
                    .date(mission.getDate())
                    .status(mission.getStatus())
                    .rejectReason(mission.getRejectReason())
                    .startedAt(mission.getStartedAt())
                    .completedAt(mission.getCompletedAt())
                    .approvedAt(mission.getApprovedAt())
                    .build();
        }
    }
}
