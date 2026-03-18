package com.hansung.adhd.repository;

import com.hansung.adhd.domain.PresetBigTasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresetBigTasksRepository extends JpaRepository<PresetBigTasks, Long> {
    List<PresetBigTasks> findByPresetIdOrderByOrderIndex(Long presetId);
}
