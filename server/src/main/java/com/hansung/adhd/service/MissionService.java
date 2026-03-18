package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.DailyMissions;
import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.DailyMissionsRepository;
import com.hansung.adhd.repository.PresetBigTasksRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final DailyMissionsRepository dailyMissionsRepository;
    private final ChildrenRepository childrenRepository;
    private final PresetBigTasksRepository presetBigTasksRepository;

    // 오늘의 미션 목록 조회
    @Transactional(readOnly = true)
    public List<MissionDto.MissionResponse> getTodayMissions(Long childId) {
        return dailyMissionsRepository.findByChildIdAndDate(childId, LocalDate.now())
                .stream()
                .map(MissionDto.MissionResponse::from)
                .collect(Collectors.toList());
    }

    // 미션 생성 (BigTask 선택 → SmallTask 자동 복사)
    @Transactional
    public MissionDto.MissionResponse createMission(MissionDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        PresetBigTasks bigTask = presetBigTasksRepository.findById(request.getOriginBigTaskId())
                .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));

        DailyMissions mission = DailyMissions.create(
                child,
                bigTask.getPreset().getId(),
                bigTask.getId(),
                bigTask.getPreset().getTitle(),
                bigTask.getTitle(),
                null,
                request.getAssignedExp(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        return MissionDto.MissionResponse.from(dailyMissionsRepository.save(mission));
    }

    // 미션 시작 (퀘스트 시작 버튼)
    @Transactional
    public MissionDto.MissionResponse startMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);
        mission.start(LocalDateTime.now());
        return MissionDto.MissionResponse.from(mission);
    }

    // 미션 완료 처리
    @Transactional
    public MissionDto.MissionResponse completeMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);
        mission.complete(LocalDateTime.now());
        return MissionDto.MissionResponse.from(mission);
    }

    // 미션 승인/거절 (부모)
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

    private DailyMissions getMissionOrThrow(Long missionId) {
        return dailyMissionsRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSION_NOT_FOUND));
    }
}