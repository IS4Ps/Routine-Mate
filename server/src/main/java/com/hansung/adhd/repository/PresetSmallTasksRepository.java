package com.hansung.adhd.repository;

import com.hansung.adhd.domain.PresetSmallTasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresetSmallTasksRepository extends JpaRepository<PresetSmallTasks, Long> {

    // BigTask의 SmallTask 목록 순서대로
    List<PresetSmallTasks> findByBigTaskIdOrderByOrderIndex(Long bigTaskId);

    // 여러 BigTask의 SmallTask 한번에 조회 (N+1 방지)
    List<PresetSmallTasks> findByBigTaskIdInOrderByOrderIndex(List<Long> bigTaskIds);
}