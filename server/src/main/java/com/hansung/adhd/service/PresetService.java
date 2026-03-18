package com.hansung.adhd.service;

import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.domain.RoutinePresets;
import com.hansung.adhd.dto.PresetDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ParentsRepository;
import com.hansung.adhd.repository.PresetBigTasksRepository;
import com.hansung.adhd.repository.PresetSmallTasksRepository;
import com.hansung.adhd.repository.RoutinePresetsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 프리셋 생성
    @Transactional
    public PresetDto.PresetResponse createPreset(PresetDto.CreateRequest request) {
        Parents parent = parentsRepository.findById(request.getParentId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        // 프리셋 생성
        RoutinePresets preset = RoutinePresets.create(
                parent,
                request.getTitle(),
                request.getDescription(),
                request.getIcon(),
                request.getDurationDays()
        );
        routinePresetsRepository.save(preset);

        // BigTask + SmallTask 생성
        if (request.getBigTasks() != null) {
            for (PresetDto.BigTaskCreateRequest bigTaskReq : request.getBigTasks()) {
                PresetBigTasks bigTask = PresetBigTasks.create(
                        preset,
                        bigTaskReq.getTitle(),
                        bigTaskReq.getIcon(),
                        bigTaskReq.getOrderIndex(),
                        bigTaskReq.getStartTime()
                );
                presetBigTasksRepository.save(bigTask);

                if (bigTaskReq.getSmallTasks() != null) {
                    for (PresetDto.SmallTaskCreateRequest smallTaskReq : bigTaskReq.getSmallTasks()) {
                        PresetSmallTasks smallTask = PresetSmallTasks.create(
                                bigTask,
                                smallTaskReq.getTitle(),
                                smallTaskReq.getTags(),
                                smallTaskReq.getDifficultyLevel(),
                                smallTaskReq.getOrderIndex()
                        );
                        presetSmallTasksRepository.save(smallTask);
                    }
                }
            }
        }

        return PresetDto.PresetResponse.from(preset);
    }

    // 프리셋 수정
    @Transactional
    public PresetDto.PresetResponse updatePreset(Long presetId, PresetDto.UpdateRequest request) {
        RoutinePresets preset = routinePresetsRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));

        preset.update(
                request.getTitle(),
                request.getDescription(),
                request.getIcon(),
                request.getDurationDays()
        );

        return PresetDto.PresetResponse.from(preset);
    }

    // 프리셋 삭제
    @Transactional
    public void deletePreset(Long presetId) {
        RoutinePresets preset = routinePresetsRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));

        preset.delete(); // 소프트 삭제
    }
}