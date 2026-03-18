package com.hansung.adhd.repository;

import com.hansung.adhd.domain.PresetBigTasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresetBigTasksRepository extends JpaRepository<PresetBigTasks, Long> {

    // 부모의 BigTask 목록 조회
    List<PresetBigTasks> findByParentId(Long parentId);

    // 프리셋에 묶인 BigTask 목록 순서대로
    List<PresetBigTasks> findByPresetIdOrderByOrderIndex(Long presetId);

    // 프리셋에 안 묶인 BigTask 목록 (독립적인 것들)
    List<PresetBigTasks> findByParentIdAndPresetIsNull(Long parentId);
}