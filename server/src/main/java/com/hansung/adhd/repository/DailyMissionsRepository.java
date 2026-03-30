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

    // 특정 기간(startDate ~ endDate) 내 아이의 미션 목록 조회
    List<DailyMissions> findByChildIdAndDateBetween(Long childId, LocalDate startDate, LocalDate endDate);
}
