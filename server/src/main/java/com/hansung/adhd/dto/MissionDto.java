package com.hansung.adhd.dto;

import com.hansung.adhd.domain.DailyMissions;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MissionDto {

    /** 미션 생성 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long      childId;
        private Long      originBigTaskId;
        private Integer   assignedExp;
        private LocalDate date;
        private String    startTime; // "08:30"
        private String    endTime;   // "09:30"
    }

    /** 미션 승인/거절 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewRequest {
        private String status;       // APPROVED or REJECTED
        private String rejectReason;
    }

    /** 미션 응답 */
    @Getter
    @Builder
    public static class MissionResponse {
        private Long          missionId;
        private String        presetTitle;
        private String        bigTaskTitle;
        private String        tags;
        private Integer       assignedExp;
        private LocalDate     date;
        private String        startTime;
        private String        endTime;
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
                    .tags(mission.getTags())
                    .assignedExp(mission.getAssignedExp())
                    .date(mission.getDate())
                    .startTime(mission.getStartTime())
                    .endTime(mission.getEndTime())
                    .status(mission.getStatus())
                    .rejectReason(mission.getRejectReason())
                    .startedAt(mission.getStartedAt())
                    .completedAt(mission.getCompletedAt())
                    .approvedAt(mission.getApprovedAt())
                    .build();
        }
    }
}