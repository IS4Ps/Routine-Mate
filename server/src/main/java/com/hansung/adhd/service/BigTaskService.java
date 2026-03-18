package com.hansung.adhd.service;

import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.domain.PresetBigTasks;
import com.hansung.adhd.domain.PresetSmallTasks;
import com.hansung.adhd.domain.RoutinePresets;
import com.hansung.adhd.dto.BigTaskDto;
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
public class BigTaskService {

    private final PresetBigTasksRepository presetBigTasksRepository;
    private final PresetSmallTasksRepository presetSmallTasksRepository;
    private final ParentsRepository parentsRepository;
    private final RoutinePresetsRepository routinePresetsRepository;

    // 부모의 BigTask 목록 조회 (SmallTask 포함)
    @Transactional(readOnly = true)
    public List<BigTaskDto.BigTaskResponse> getBigTasks(Long parentId) {
        List<PresetBigTasks> bigTasks = presetBigTasksRepository.findByParentId(parentId);

        List<Long> bigTaskIds = bigTasks.stream()
                .map(PresetBigTasks::getId)
                .toList();

        Map<Long, List<BigTaskDto.SmallTaskResponse>> smallTaskMap =
                presetSmallTasksRepository.findByBigTaskIdInOrderByOrderIndex(bigTaskIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getBigTask().getId(),
                                Collectors.mapping(BigTaskDto.SmallTaskResponse::from, Collectors.toList())
                        ));

        return bigTasks.stream()
                .map(bigTask -> BigTaskDto.BigTaskResponse.from(
                        bigTask,
                        smallTaskMap.getOrDefault(bigTask.getId(), List.of())
                ))
                .toList();
    }

    // BigTask 생성 (SmallTask 포함)
    @Transactional
    public BigTaskDto.BigTaskResponse createBigTask(BigTaskDto.CreateRequest request) {
        Parents parent = parentsRepository.findById(request.getParentId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        PresetBigTasks bigTask = PresetBigTasks.create(
                parent,
                request.getTitle(),
                request.getIcon(),
                request.getOrderIndex(),
                request.getStartTime(),
                request.getEndTime()
        );
        presetBigTasksRepository.save(bigTask);

        // SmallTask 생성
        List<PresetSmallTasks> smallTasks = List.of();
        if (request.getSmallTasks() != null) {
            smallTasks = request.getSmallTasks().stream()
                    .map(st -> PresetSmallTasks.create(
                            bigTask,
                            st.getTitle(),
                            st.getTags(),
                            st.getDifficultyLevel(),
                            st.getOrderIndex()
                    ))
                    .collect(Collectors.toList());
            presetSmallTasksRepository.saveAll(smallTasks);
        }

        List<BigTaskDto.SmallTaskResponse> smallTaskResponses = smallTasks.stream()
                .map(BigTaskDto.SmallTaskResponse::from)
                .toList();

        return BigTaskDto.BigTaskResponse.from(bigTask, smallTaskResponses);
    }

    // BigTask 수정
    @Transactional
    public BigTaskDto.BigTaskResponse updateBigTask(Long bigTaskId, BigTaskDto.UpdateRequest request) {
        PresetBigTasks bigTask = presetBigTasksRepository.findById(bigTaskId)
                .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));

        bigTask.update(request.getTitle(), request.getIcon(),
                request.getOrderIndex(), request.getStartTime(), request.getEndTime());

        List<BigTaskDto.SmallTaskResponse> smallTaskResponses =
                presetSmallTasksRepository.findByBigTaskIdOrderByOrderIndex(bigTaskId)
                        .stream()
                        .map(BigTaskDto.SmallTaskResponse::from)
                        .toList();

        return BigTaskDto.BigTaskResponse.from(bigTask, smallTaskResponses);
    }

    // BigTask 삭제
    @Transactional
    public void deleteBigTask(Long bigTaskId) {
        PresetBigTasks bigTask = presetBigTasksRepository.findById(bigTaskId)
                .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));
        bigTask.delete();
    }

    // BigTask를 프리셋에 묶기
    @Transactional
    public BigTaskDto.BigTaskResponse assignToPreset(Long bigTaskId, Long presetId) {
        PresetBigTasks bigTask = presetBigTasksRepository.findById(bigTaskId)
                .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));

        RoutinePresets preset = routinePresetsRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));

        bigTask.assignToPreset(preset);

        List<BigTaskDto.SmallTaskResponse> smallTaskResponses =
                presetSmallTasksRepository.findByBigTaskIdOrderByOrderIndex(bigTaskId)
                        .stream()
                        .map(BigTaskDto.SmallTaskResponse::from)
                        .toList();

        return BigTaskDto.BigTaskResponse.from(bigTask, smallTaskResponses);
    }
}