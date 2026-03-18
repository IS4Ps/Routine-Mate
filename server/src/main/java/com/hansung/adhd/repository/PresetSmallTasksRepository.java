package com.hansung.adhd.repository;

import com.hansung.adhd.domain.PresetSmallTasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresetSmallTasksRepository extends JpaRepository<PresetSmallTasks, Long> {
    List<PresetSmallTasks> findByBigTaskIdInOrderByOrderIndex(List<Long> bigTaskIds);
}
