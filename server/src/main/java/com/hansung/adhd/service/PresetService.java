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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresetService {

    private final RoutinePresetsRepository routinePresetsRepository;
    private final PresetBigTasksRepository presetBigTasksRepository;
    private final PresetSmallTasksRepository presetSmallTasksRepository;
    private final ParentsRepository parentsRepository;
    private final ChildrenRepository childrenRepository;
    private final DailyMissionsRepository dailyMissionsRepository;

    // 부모의 프리셋 목록 조회
    @Transactional(readOnly = true)
    public List<PresetDto.PresetResponse> getPresets(Long parentId) {
        parentsRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        return routinePresetsRepository.findByParentId(parentId)
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

    // 날짜 기준 프리셋 저장 (선택한 날짜의 미션들 → 프리셋)
    @Transactional
    public PresetDto.PresetResponse saveFromDate(PresetDto.SaveFromDateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        Parents parent = child.getParent();
        if (parent == null) throw new CustomException(ErrorCode.PARENT_NOT_FOUND);

        List<DailyMissions> missions = dailyMissionsRepository
                .findByChildIdAndDate(request.getChildId(), request.getDate());

        if (missions.isEmpty()) throw new CustomException(ErrorCode.MISSION_NOT_FOUND);

        List<Long> bigTaskIds = missions.stream()
                .filter(m -> m.getOriginBigTaskId() != null)
                .map(DailyMissions::getOriginBigTaskId)
                .distinct()
                .collect(Collectors.toList());

        RoutinePresets preset = RoutinePresets.create(
                parent, request.getTitle(), request.getDescription(), null, 1);
        routinePresetsRepository.save(preset);

        bigTaskIds.forEach(bigTaskId -> {
            PresetBigTasks bigTask = presetBigTasksRepository.findById(bigTaskId)
                    .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));
            bigTask.assignToPreset(preset);
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

        // BigTask들의 원본 날짜 중 가장 빠른 날짜를 기준일로 잡음
        // (프리셋 저장 시 BigTask들이 특정 날짜 기반으로 만들어진 경우)
        // 현재는 BigTask에 날짜 정보가 없으므로 순서(orderIndex)를 날짜 offset으로 사용
        LocalDate startDate = request.getStartDate();

        List<DailyMissions> missions = bigTasks.stream()
                .map(bigTask -> {
                    // orderIndex를 dayOffset으로 활용 (0부터 시작)
                    int dayOffset = bigTask.getOrderIndex() != null ? bigTask.getOrderIndex() : 0;
                    LocalDate missionDate = startDate.plusDays(dayOffset);

                    return DailyMissions.create(
                            child,
                            preset.getId(),
                            bigTask.getId(),
                            preset.getTitle(),
                            bigTask.getTitle(),
                            null,
                            request.getAssignedExpPerMission(),
                            missionDate,
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
