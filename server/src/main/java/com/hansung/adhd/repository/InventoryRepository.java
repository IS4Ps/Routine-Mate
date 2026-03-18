package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 아이 인벤토리 전체 조회
    List<Inventory> findByChildId(Long childId);

    // 특정 아이템 보유 여부 확인 (중복 구매 방지)
    Optional<Inventory> findByChildIdAndItemId(Long childId, Long itemId);

    // 같은 타입 중 장착된 아이템 조회 (장착 시 기존 해제용)
    @Query("SELECT inv FROM Inventory inv " +
           "JOIN inv.item i " +
           "WHERE inv.child.id = :childId " +
           "AND i.type = :type " +
           "AND inv.isEquipped = true")
    Optional<Inventory> findEquippedItemByType(@Param("childId") Long childId,
                                               @Param("type") String type);
}
