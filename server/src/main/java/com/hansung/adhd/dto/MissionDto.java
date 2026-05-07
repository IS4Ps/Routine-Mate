package com.hansung.adhd.dto;

import com.hansung.adhd.domain.DailyMissions;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        private String    startTime;
        private String    endTime;
    }

    /** 미션 승인/거절 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewRequest {
        private String status;
        private String rejectReason;
    }

    /** 미션 내 SmallTask */
    @Getter
    @Builder
    public static class SmallTaskResponse {
        private Long    smallTaskId;
        private String  title;
        private Integer orderIndex;

        public static SmallTaskResponse from(com.hansung.adhd.domain.PresetSmallTasks s) {
            return SmallTaskResponse.builder()
                    .smallTaskId(s.getId())
                    .title(s.getTitle())
                    .orderIndex(s.getOrderIndex())
                    .build();
        }
    }

    /** 미션 응답 */
    @Getter
    @Builder
    public static class MissionResponse {
        private Long          missionId;
        private Long          originBigTaskId;
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
        private List<SmallTaskResponse> smallTasks;

        public static MissionResponse from(DailyMissions mission, List<SmallTaskResponse> smallTasks) {
            return MissionResponse.builder()
                    .missionId(mission.getId())
                    .originBigTaskId(mission.getOriginBigTaskId())
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
                    .smallTasks(smallTasks)
                    .build();
        }
    }

    /** 일별 달성 현황 */
    @Getter
    @Builder
    public static class DailyAchievement {
        private LocalDate date;
        private Integer   totalCount;      // 오늘 전체 미션 수
        private Integer   completedCount;  // 완료한 미션 수
        private Double    completionRate;  // 달성률 (%)
        private Boolean   isSuccess;       // 70% 이상이면 오늘 성공
    }

    /** 주간 성공률 응답 */
    @Getter
    @Builder
    public static class WeeklyStatsResponse {
        private LocalDate startDate;         // 주 시작일 (월요일)
        private LocalDate endDate;           // 주 종료일 (일요일)
        private Integer   successDays;       // 성공한 날 수
        private Integer   totalDays;         // 전체 일수 (7)
        private Double    weeklySuccessRate; // 주간 성공률 (successDays/7 * 100)
        private Double    avgCompletionRate; // 평균 달성률
        private Boolean   isRewardEligible;  // 보상 지급 대상 여부 (5일 이상 성공)
        private List<DailyAchievement> dailyList; // 일별 상세 현황
    }

    /** 기존 통계 응답 (하위 호환용) */
    @Getter
    @Builder
    public static class StatisticsResponse {
        private Integer totalMissions;
        private Integer completedMissions;
        private Double  completionRate;
    }
}