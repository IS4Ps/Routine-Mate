package com.hansung.adhd.service;

import com.hansung.adhd.domain.*;
import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.DailyMissionsRepository;
import com.hansung.adhd.repository.PresetBigTasksRepository;
import com.hansung.adhd.repository.RoutinePresetsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final DailyMissionsRepository dailyMissionsRepository;
    private final ChildrenRepository childrenRepository;
    private final PresetBigTasksRepository presetBigTasksRepository;
    private final RoutinePresetsRepository routinePresetsRepository;

    // 오늘의 미션 목록 조회
    @Transactional(readOnly = true)
    public List<MissionDto.MissionResponse> getTodayMissions(Long childId) {
        return dailyMissionsRepository.findByChildIdAndDate(childId, LocalDate.now())
                .stream()
                .map(MissionDto.MissionResponse::from)
                .collect(Collectors.toList());
    }

    // 미션 생성 (BigTask 선택 → tags 자동 복사)
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
                bigTask.getTags(),  // BigTask의 tags 자동 복사
                request.getAssignedExp(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        return MissionDto.MissionResponse.from(dailyMissionsRepository.save(mission));
    }

    // 미션 시작
    @Transactional
    public MissionDto.MissionResponse startMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);
        mission.start(LocalDateTime.now());
        return MissionDto.MissionResponse.from(mission);
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

        return MissionDto.MissionResponse.from(mission);
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
        return MissionDto.MissionResponse.from(mission);
    }

    // 미션 삭제
    @Transactional
    public void deleteMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);
        mission.delete();
    }

    // 주간 통계
    @Transactional(readOnly = true)
    public MissionDto.StatisticsResponse getWeeklyStatistics(Long childId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(7);

        List<DailyMissions> weeklyMissions =
                dailyMissionsRepository.findByChildIdAndDateBetween(childId, startOfWeek, today);

        int total = weeklyMissions.size();
        int completed = (int) weeklyMissions.stream()
                .filter(m -> "COMPLETED".equals(m.getStatus()) || "APPROVED".equals(m.getStatus()))
                .count();

        double rate = (total == 0) ? 0.0 : Math.round(((double) completed / total) * 1000) / 10.0;

        return MissionDto.StatisticsResponse.builder()
                .totalMissions(total)
                .completedMissions(completed)
                .completionRate(rate)
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

            List<RoutinePresets> parentPresets = routinePresetsRepository.findByParentId(parent.getId());

            for (RoutinePresets preset : parentPresets) {
                List<PresetBigTasks> bigTasks =
                        presetBigTasksRepository.findByPresetIdOrderByOrderIndex(preset.getId());

                for (PresetBigTasks bigTask : bigTasks) {
                    DailyMissions newMission = DailyMissions.create(
                            child,
                            preset.getId(),
                            bigTask.getId(),
                            preset.getTitle(),
                            bigTask.getTitle(),
                            bigTask.getTags(),  // tags 자동 복사
                            20,
                            today,
                            bigTask.getStartTime(),
                            bigTask.getEndTime()
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
}