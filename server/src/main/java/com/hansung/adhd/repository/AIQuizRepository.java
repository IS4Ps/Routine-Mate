package com.hansung.adhd.repository;

import com.hansung.adhd.domain.AIGeneratedQuizzes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIQuizRepository extends JpaRepository<AIGeneratedQuizzes, Long> {

    // 아이의 퀴즈 목록 (최신순)
    List<AIGeneratedQuizzes> findByChildIdOrderByCreatedAtDesc(Long childId);

    // 아직 안 푼 퀴즈 목록
    List<AIGeneratedQuizzes> findByChildIdAndIsSolvedFalse(Long childId);
}
