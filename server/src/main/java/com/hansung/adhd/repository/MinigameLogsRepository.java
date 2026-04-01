package com.hansung.adhd.repository;

import com.hansung.adhd.domain.MinigameLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MinigameLogsRepository extends JpaRepository<MinigameLogs, Long> {
    List<MinigameLogs> findByChildIdOrderByCreatedAtDesc(Long childId);
    List<MinigameLogs> findByChildIdAndGameType(Long childId, String gameType);
}
