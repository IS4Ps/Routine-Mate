package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.DailyMissions;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.RoutinePresets;
import com.hansung.adhd.domain.PresetSmallTasks;
import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.dto.PresetDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.DailyMissionsRepository;
import com.hansung.adhd.repository.ParentsRepository;
import com.hansung.adhd.repository.PresetBigTasksRepository;
import com.hansung.adhd.repository.PresetSmallTasksRepository;
import com.hansung.adhd.repository.RoutinePresetsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PresetService {

    private final RoutinePresetsRepository routinePresetsRepository;
    private final PresetBigTasksRepository presetBigTasksRepository;
    private final PresetSmallTasksRepository presetSmallTasksRepository;
    private final ParentsRepository parentsRepository;
    private final ChildrenRepository childrenRepository;
    private final DailyMissionsRepository dailyMissionsRepository;

    // 부모의 프리셋 목록 조회 (parentId 기준)
    @Transactional(readOnly = true)
    public List<PresetDto.PresetResponse> getPresets(Long parentId) {
        parentsRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        return routinePresetsRepository.findByParentIdAndIsDeletedFalse(parentId)
                .stream()
                .map(PresetDto.PresetResponse::from)
                .toList();
    }

    // 아이 ID로 프리셋 목록 조회 (save-from-date 이후 바로 목록 조회 시 사용)
    @Transactional(readOnly = true)
    public List<PresetDto.PresetResponse> getPresetsByChildId(Long childId) {
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        Parents parent = child.getParent();
        if (parent == null) throw new CustomException(ErrorCode.PARENT_NOT_FOUND);

        return routinePresetsRepository.findByParentIdAndIsDeletedFalse(parent.getId())
                .stream()
                .map(PresetDto.PresetResponse::from)
                .toList();
    }

    // 프리셋 세부 조회 (BigTask + SmallTask 묶음)
    @Transactional(readOnly = true)
    public List<PresetDto.BigTaskResponse> getPresetDetail(Long presetId) {
        routinePresetsRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));

        List<PresetBigTasks> bigTasks =
                presetBigTasksRepository.findByPresetIdOrderByOrderIndex(presetId);

        List<Long> bigTaskIds = bigTasks.stream()
                .map(PresetBigTasks::getId)
                .toList();

        Map<Long, List<PresetDto.SmallTaskResponse>> smallTaskMap =
                presetSmallTasksRepository.findByBigTaskIdInOrderByOrderIndex(bigTaskIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getBigTask().getId(),
                                Collectors.mapping(PresetDto.SmallTaskResponse::from, Collectors.toList())
                        ));

        return bigTasks.stream()
                .map(bigTask -> PresetDto.BigTaskResponse.from(
                        bigTask,
                        smallTaskMap.getOrDefault(bigTask.getId(), List.of())
                ))
                .toList();
    }

    // 프리셋 생성 (BigTask들을 묶어서)
    @Transactional
    public PresetDto.PresetResponse createPreset(PresetDto.CreateRequest request) {
        Parents parent = parentsRepository.findById(request.getParentId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        RoutinePresets preset = RoutinePresets.create(
                parent,
                request.getTitle(),
                request.getDescription(),
                request.getIcon(),
                request.getDurationDays()
        );
        routinePresetsRepository.save(preset);

        // BigTask들을 프리셋에 묶기
        if (request.getBigTaskIds() != null) {
            request.getBigTaskIds().forEach(bigTaskId -> {
                PresetBigTasks bigTask = presetBigTasksRepository.findById(bigTaskId)
                        .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));
                bigTask.assignToPreset(preset);
            });
        }

        return PresetDto.PresetResponse.from(preset);
    }

    // 프리셋 수정
    @Transactional
    public PresetDto.PresetResponse updatePreset(Long presetId, PresetDto.UpdateRequest request) {
        RoutinePresets preset = routinePresetsRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));

        preset.update(request.getTitle(), request.getDescription(),
                request.getIcon(), request.getDurationDays());

        return PresetDto.PresetResponse.from(preset);
    }

    // 날짜 기준 프리셋 저장 (선택한 날짜 범위의 미션들 → 프리셋)
    @Transactional
    public PresetDto.PresetResponse saveFromDate(PresetDto.SaveFromDateRequest request) {
        childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        Parents parent = parentsRepository.findById(request.getParentId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        LocalDate startDate = request.getStartDate();
        LocalDate endDate   = request.getEndDate() != null ? request.getEndDate() : startDate;
        int durationDays    = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // 날짜 범위 내 전체 미션 조회
        List<DailyMissions> missions = dailyMissionsRepository
                .findByChildIdAndDateBetweenAndIsDeletedFalse(request.getChildId(), startDate, endDate);

        if (missions.isEmpty()) throw new CustomException(ErrorCode.MISSION_NOT_FOUND);

        RoutinePresets preset = RoutinePresets.create(
                parent, request.getTitle(), request.getDescription(), null, durationDays);
        routinePresetsRepository.save(preset);

        // 날짜별 dayIndex 계산해서 BigTask에 할당
        missions.stream()
                .filter(m -> m.getOriginBigTaskId() != null)
                .collect(Collectors.toMap(
                        DailyMissions::getOriginBigTaskId,
                        m -> (int) ChronoUnit.DAYS.between(startDate, m.getDate()),
                        (existing, replacement) -> existing  // 중복 bigTaskId는 첫 번째 유지
                ))
                .forEach((bigTaskId, dayIndex) -> {
                    PresetBigTasks bigTask = presetBigTasksRepository.findById(bigTaskId)
                            .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));
                    bigTask.assignToPresetWithDayIndex(preset, dayIndex);
                });

        return PresetDto.PresetResponse.from(preset);
    }

    // 프리셋 삭제
    @Transactional
    public void deletePreset(Long presetId) {
        RoutinePresets preset = routinePresetsRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));
        preset.delete();
    }

    // ── 프리셋 불러오기 ───────────────────────────────────────────────────────
    // 프리셋에 묶인 BigTask들을 선택한 날짜부터 offset 계산해서 DailyMissions 일괄 생성
    @Transactional
    public List<MissionDto.MissionResponse> loadPreset(Long presetId, PresetDto.LoadRequest request) {
        RoutinePresets preset = routinePresetsRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));

        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        // 프리셋에 묶인 BigTask 목록 조회 (순서대로)
        List<PresetBigTasks> bigTasks =
                presetBigTasksRepository.findByPresetIdOrderByOrderIndex(preset.getId());

        if (bigTasks.isEmpty()) {
            throw new CustomException(ErrorCode.PRESET_NOT_FOUND);
        }

        LocalDate startDate = request.getStartDate();

        List<DailyMissions> missions = bigTasks.stream()
                .map(bigTask -> {
                    int dayOffset = bigTask.getDayIndex() != null ? bigTask.getDayIndex() : 0;
                    return DailyMissions.create(
                            child,
                            preset.getId(),
                            bigTask.getId(),
                            preset.getTitle(),
                            bigTask.getTitle(),
                            null,
                            request.getAssignedExpPerMission(),
                            startDate.plusDays(dayOffset),
                            bigTask.getStartTime(),
                            bigTask.getEndTime()
                    );
                })
                .collect(Collectors.toList());

        dailyMissionsRepository.saveAll(missions);

        return missions.stream()
                .map(m -> MissionDto.MissionResponse.from(m, List.of()))
                .collect(Collectors.toList());
    }
}
