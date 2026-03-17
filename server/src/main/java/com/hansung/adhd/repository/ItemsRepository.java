package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemsRepository extends JpaRepository<Items, Long> {

    // 상점 조회 - 타입/레벨/직업 조건 필터링
    @Query("SELECT i FROM Items i WHERE " +
           "(:type IS NULL OR i.type = :type) AND " +
           "i.requiredLevel <= :childLevel AND " +
           "(i.requiredJob IS NULL OR i.requiredJob.id = :childJobId)")
    List<Items> findShopItems(@Param("type") String type,
                              @Param("childLevel") Integer childLevel,
                              @Param("childJobId") Long childJobId);
}
