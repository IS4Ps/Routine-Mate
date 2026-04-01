package com.hansung.adhd.repository;

import com.hansung.adhd.domain.OfflineRewards;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfflineRewardsRepository extends JpaRepository<OfflineRewards, Long> {
    List<OfflineRewards> findByChildId(Long childId);
    List<OfflineRewards> findByChildIdAndStatus(Long childId, String status);
}
