package com.hansung.adhd.repository;

import com.hansung.adhd.domain.DailyMissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyMissionsRepository extends JpaRepository<DailyMissions, Long> {

    // 오늘의 미션 목록
    List<DailyMissions> findByChildIdAndDate(Long childId, LocalDate date);

    // 특정 상태의 미션 목록
    List<DailyMissions> findByChildIdAndStatus(Long childId, String status);
}
