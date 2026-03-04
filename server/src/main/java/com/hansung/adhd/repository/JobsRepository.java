package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Jobs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobsRepository extends JpaRepository<Jobs, Long> {
}
