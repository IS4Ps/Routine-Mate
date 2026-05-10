package com.hansung.adhd.service;

import com.hansung.adhd.domain.*;
import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.DailyMissionsRepository;
import com.hansung.adhd.repository.PresetBigTasksRepository;
import com.hansung.adhd.repository.PresetSmallTasksRepository;
import com.hansung.adhd.repository.RoutinePresetsRepository;
import com.hansung.adhd.response.ErrorCode;
import com.hansung.adhd.dto.AiRoutineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final DailyMissionsRepository dailyMissionsRepository;
    private final ChildrenRepository childrenRepository;
    private final PresetBigTasksRepository presetBigTasksRepository;
    private final PresetSmallTasksRepository presetSmallTasksRepository;
    private final RoutinePresetsRepository routinePresetsRepository;

    private static final double SUCCESS_THRESHOLD = 70.0; // 일별 성공 기준 달성률
    private static final int REWARD_THRESHOLD = 5;        // 주간 보상 기준 성공 일수

    // 오늘의 미션 목록 조회
    @Transactional(readOnly = true)
    public List<MissionDto.MissionResponse> getTodayMissions(Long childId) {
        List<DailyMissions> missions = dailyMissionsRepository.findByChildIdAndDateAndIsDeletedFalse(childId, LocalDate.now());

        List<Long> bigTaskIds = missions.stream()
                .filter(m -> m.getOriginBigTaskId() != null)
                .map(DailyMissions::getOriginBigTaskId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, List<MissionDto.SmallTaskResponse>> smallTaskMap =
                presetSmallTasksRepository.findByBigTaskIdInOrderByOrderIndex(bigTaskIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getBigTask().getId(),
                                Collectors.mapping(MissionDto.SmallTaskResponse::from, Collectors.toList())
                        ));

        return missions.stream()
                .map(m -> MissionDto.MissionResponse.from(
                        m,
                        smallTaskMap.getOrDefault(m.getOriginBigTaskId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    // 날짜별 미션 목록 조회
    @Transactional(readOnly = true)
    public List<MissionDto.MissionResponse> getMissionsByDate(Long childId, LocalDate date) {
        List<DailyMissions> missions = dailyMissionsRepository.findByChildIdAndDateAndIsDeletedFalse(childId, date);

        List<Long> bigTaskIds = missions.stream()
                .filter(m -> m.getOriginBigTaskId() != null)
                .map(DailyMissions::getOriginBigTaskId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, List<MissionDto.SmallTaskResponse>> smallTaskMap =
                presetSmallTasksRepository.findByBigTaskIdInOrderByOrderIndex(bigTaskIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getBigTask().getId(),
                                Collectors.mapping(MissionDto.SmallTaskResponse::from, Collectors.toList())
                        ));

        return missions.stream()
                .map(m -> MissionDto.MissionResponse.from(
                        m,
                        smallTaskMap.getOrDefault(m.getOriginBigTaskId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    // 미션 생성
    @Transactional
    public MissionDto.MissionResponse createMission(MissionDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        PresetBigTasks bigTask = presetBigTasksRepository.findById(request.getOriginBigTaskId())
                .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));

        DailyMissions mission = DailyMissions.create(
                child,
                bigTask.getPreset() != null ? bigTask.getPreset().getId() : null,
                bigTask.getId(),
                bigTask.getPreset() != null ? bigTask.getPreset().getTitle() : null,
                bigTask.getTitle(),
                bigTask.getTags(),
                request.getAssignedExp(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        return MissionDto.MissionResponse.from(dailyMissionsRepository.save(mission), List.of());
    }

    // 미션 시작
    @Transactional
    public MissionDto.MissionResponse startMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);
        mission.start(LocalDateTime.now());
        return MissionDto.MissionResponse.from(mission, List.of());
    }

    // 미션 완료
    @Transactional
    public MissionDto.MissionResponse completeMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);

        if ("COMPLETED".equals(mission.getStatus())) {
            throw new CustomException(ErrorCode.INVALID_MISSION_STATUS);
        }

        mission.complete(LocalDateTime.now());

        Children child = mission.getChild();
        child.gainExp(mission.getAssignedExp());
        child.addGold(30);

        return MissionDto.MissionResponse.from(mission, List.of());
    }

    // 미션 승인/거절
    @Transactional
    public MissionDto.MissionResponse reviewMission(Long missionId, MissionDto.ReviewRequest request) {
        DailyMissions mission = getMissionOrThrow(missionId);
        if ("APPROVED".equals(request.getStatus())) {
            mission.approve(LocalDateTime.now());
        } else if ("REJECTED".equals(request.getStatus())) {
            mission.reject(request.getRejectReason());
        } else {
            throw new CustomException(ErrorCode.INVALID_MISSION_STATUS);
        }
        return MissionDto.MissionResponse.from(mission, List.of());
    }

    // 미션 삭제
    @Transactional
    public void deleteMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);
        mission.delete();
    }

    // ── 주간 성공률 통계 ─────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public MissionDto.WeeklyStatsResponse getWeeklyStats(Long childId) {
        LocalDate today = LocalDate.now();

        // 이번 주 월요일 ~ 일요일 계산
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        // 이번 주 전체 미션 조회
        List<DailyMissions> weeklyMissions =
                dailyMissionsRepository.findByChildIdAndDateBetweenAndIsDeletedFalse(childId, monday, sunday);

        // 날짜별로 그룹핑
        Map<LocalDate, List<DailyMissions>> missionsByDate = weeklyMissions.stream()
                .collect(Collectors.groupingBy(DailyMissions::getDate));

        // 일별 달성 현황 계산
        List<MissionDto.DailyAchievement> dailyList = new ArrayList<>();
        int successDays = 0;
        double totalRate = 0.0;

        for (LocalDate date = monday; !date.isAfter(sunday); date = date.plusDays(1)) {
            List<DailyMissions> dayMissions = missionsByDate.getOrDefault(date, List.of());

            int total = dayMissions.size();
            int completed = (int) dayMissions.stream()
                    .filter(m -> "COMPLETED".equals(m.getStatus()) || "APPROVED".equals(m.getStatus()))
                    .count();

            double rate = (total == 0) ? 0.0 : Math.round(((double) completed / total) * 1000) / 10.0;
            boolean isSuccess = total > 0 && rate >= SUCCESS_THRESHOLD;

            if (isSuccess) successDays++;
            totalRate += rate;

            dailyList.add(MissionDto.DailyAchievement.builder()
                    .date(date)
                    .totalCount(total)
                    .completedCount(completed)
                    .completionRate(rate)
                    .isSuccess(isSuccess)
                    .build());
        }

        double avgRate = Math.round((totalRate / 7) * 10) / 10.0;
        double weeklySuccessRate = Math.round(((double) successDays / 7) * 1000) / 10.0;

        return MissionDto.WeeklyStatsResponse.builder()
                .startDate(monday)
                .endDate(sunday)
                .successDays(successDays)
                .totalDays(7)
                .weeklySuccessRate(weeklySuccessRate)
                .avgCompletionRate(avgRate)
                .isRewardEligible(successDays >= REWARD_THRESHOLD)
                .dailyList(dailyList)
                .build();
    }

    // 스케줄러용
    @Transactional
    public void generateDailyMissionsFromPresets() {
        log.info("[배치 작업 시작] 오늘의 미션 자동 생성");
        LocalDate today = LocalDate.now();

        List<Children> allChildren = childrenRepository.findAll();

        for (Children child : allChildren) {
            Parents parent = child.getParent();
            if (parent == null) continue;

            List<RoutinePresets> parentPresets = routinePresetsRepository.findByParentIdAndIsDeletedFalse(parent.getId());

            for (RoutinePresets preset : parentPresets) {
                List<PresetBigTasks> bigTasks =
                        presetBigTasksRepository.findByPresetIdOrderByOrderIndex(preset.getId());

                for (PresetBigTasks bigTask : bigTasks) {
                    if (dailyMissionsRepository.existsByChildIdAndOriginBigTaskIdAndDateAndIsDeletedFalse(
                            child.getId(), bigTask.getId(), today)) {
                        continue;
                    }
                    DailyMissions newMission = DailyMissions.create(
                            child, preset.getId(), bigTask.getId(),
                            preset.getTitle(), bigTask.getTitle(),
                            bigTask.getTags(), 20, today,
                            bigTask.getStartTime(), bigTask.getEndTime()
                    );
                    dailyMissionsRepository.save(newMission);
                }
            }
        }
        log.info("[배치 작업 완료]");
    }

    private DailyMissions getMissionOrThrow(Long missionId) {
        return dailyMissionsRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSION_NOT_FOUND));
    }

    // ── AI 추천 루틴을 실제 미션으로 자동 생성 ─────────────────────────────
    @Transactional
    public List<MissionDto.MissionResponse> createMissionsFromAi(Long childId, List<AiRoutineDto.Recommendation> recommendations) {
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        List<DailyMissions> newMissions = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // AI가 추천해준 목록을 쓱쓱 돌면서 DailyMission 엔티티로 변환!
        for (AiRoutineDto.Recommendation rec : recommendations) {
            DailyMissions mission = DailyMissions.create(
                    child,
                    null, // AI가 만든 거라 원본 Preset 없음
                    null, // 원본 BigTask 없음
                    "AI 맞춤 추천 루틴", // presetTitle
                    rec.getTitle(), // AI가 지어준 제목
                    "AI추천,맞춤형", // tags
                    rec.getExp(), // AI가 책정한 경험치
                    today,
                    null, // startTime
                    null  // endTime
            );
            newMissions.add(mission);
        }

        // DB에 일괄 저장
        dailyMissionsRepository.saveAll(newMissions);

        // 깔끔하게 DTO로 변환해서 리턴
        return newMissions.stream()
                .map(mission -> MissionDto.MissionResponse.from(mission, java.util.Collections.emptyList()))
                .collect(java.util.stream.Collectors.toList());
    }
}