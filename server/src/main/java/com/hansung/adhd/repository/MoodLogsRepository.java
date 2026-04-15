package com.hansung.adhd.repository;

import com.hansung.adhd.domain.MoodLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodLogsRepository extends JpaRepository<MoodLogs, Long> {

    // 전체 목록 (최신순)
    List<MoodLogs> findByChildIdOrderByDateDesc(Long childId);

    // 특정 날짜 조회
    Optional<MoodLogs> findByChildIdAndDate(Long childId, LocalDate date);

    // 기간 조회 (월간 캘린더용)
    List<MoodLogs> findByChildIdAndDateBetweenOrderByDate(Long childId,
                                                          LocalDate startDate,
                                                          LocalDate endDate);
}