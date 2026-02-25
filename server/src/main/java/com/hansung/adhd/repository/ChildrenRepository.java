package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Children;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildrenRepository extends JpaRepository<Children, Long> {
}