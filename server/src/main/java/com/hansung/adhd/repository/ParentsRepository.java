package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Parents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentsRepository extends JpaRepository<Parents, Long> {
}