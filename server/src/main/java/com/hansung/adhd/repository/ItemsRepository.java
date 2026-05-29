package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemsRepository extends JpaRepository<Items, Long> {

    // 상점 조회 - 타입/직업 조건 필터링 (레벨 조건 제거 - 프론트에서 구매 버튼 제어)
    @Query("SELECT i FROM Items i WHERE " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(i.requiredJob IS NULL OR i.requiredJob.id = :childJobId)")
    List<Items> findShopItems(@Param("type") String type,
                              @Param("childJobId") Long childJobId);
}
