package com.hansung.adhd.repository;

import com.hansung.adhd.domain.MoodLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodLogsRepository extends JpaRepository<MoodLogs, Long> {
    List<MoodLogs> findByChildIdOrderByDateDesc(Long childId);
    Optional<MoodLogs> findByChildIdAndDate(Long childId, LocalDate date);
}
