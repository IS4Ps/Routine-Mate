package com.hansung.adhd.repository;

import com.hansung.adhd.domain.RoutinePresets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutinePresetsRepository extends JpaRepository<RoutinePresets, Long> {
    List<RoutinePresets> findByParentIdAndIsDeletedFalse(Long parentId);
}
