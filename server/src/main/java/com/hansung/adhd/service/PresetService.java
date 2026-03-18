package com.hansung.adhd.service;

import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import com.hansung.adhd.dto.PresetDto;
import com.hansung.adhd.exception.CustomException;
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

    // 부모의 프리셋 목록 조회
    @Transactional(readOnly = true)
    public List<PresetDto.PresetResponse> getPresets(Long parentId) {
        return routinePresetsRepository.findByParentId(parentId)
                .stream()
                .map(PresetDto.PresetResponse::from)
                .toList();
    }

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
}