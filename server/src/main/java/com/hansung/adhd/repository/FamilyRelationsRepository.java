package com.hansung.adhd.repository;

import com.hansung.adhd.domain.FamilyRelations;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository를 상속받는 순간, 이 녀석은 Insert, Select, Update, Delete를 알아서 척척 해내는 만능 일꾼이 됨
public interface FamilyRelationsRepository extends JpaRepository<FamilyRelations, Long> {
}