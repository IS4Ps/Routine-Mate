package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.Inventory;
import com.hansung.adhd.domain.Items;
import com.hansung.adhd.dto.InventoryDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.InventoryRepository;
import com.hansung.adhd.repository.ItemsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemsRepository itemsRepository;
    private final ChildrenRepository childrenRepository;

    // 인벤토리 전체 조회
    @Transactional(readOnly = true)
    public List<InventoryDto.InventoryResponse> getInventory(Long childId) {
        return inventoryRepository.findByChildId(childId)
                .stream()
                .map(InventoryDto.InventoryResponse::from)
                .toList();
    }

    // 현재 장착 아이템만 조회 (앱 시작 시 캐릭터 외형 복원용)
    @Transactional(readOnly = true)
    public List<InventoryDto.InventoryResponse> getEquippedItems(Long childId) {
        return inventoryRepository.findByChildId(childId)
                .stream()
                .filter(inv -> Boolean.TRUE.equals(inv.getIsEquipped()))
                .map(InventoryDto.InventoryResponse::from)
                .toList();
    }

    // 아이템 구매
    @Transactional
    public InventoryDto.PurchaseResponse purchaseItem(Long itemId, Long childId) {
        Items item = itemsRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        // 중복 구매 방지
        inventoryRepository.findByChildIdAndItemId(childId, itemId)
                .ifPresent(inv -> { throw new CustomException(ErrorCode.ITEM_ALREADY_OWNED); });

        // 레벨 조건 검증 (임시 비활성화)
//        if (child.getLevel() < item.getRequiredLevel()) {
//            throw new CustomException(ErrorCode.INSUFFICIENT_LEVEL);
//        }

        // 직업 조건 검증 (임시 비활성화)
//        if (item.getRequiredJob() != null) {
//            if (child.getJob() == null ||
//                    !item.getRequiredJob().getId().equals(child.getJob().getId())) {
//                throw new CustomException(ErrorCode.JOB_NOT_MATCHED);
//            }
//        }

        // 골드 차감
        child.useGold(item.getPrice());

        Inventory inventory = Inventory.create(child, item);
        inventoryRepository.save(inventory);

        return InventoryDto.PurchaseResponse.builder()
                .inventoryId(inventory.getId())
                .itemId(item.getId())
                .itemName(item.getName())
                .remainingGold(child.getGold())
                .build();
    }

    // 아이템 장착/해제
    @Transactional
    public InventoryDto.EquipResponse equipItem(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        // 이미 장착 중이면 해제
        if (inventory.getIsEquipped()) {
            inventory.unequip();
            return InventoryDto.EquipResponse.builder()
                    .inventoryId(inventory.getId())
                    .itemType(inventory.getItem().getType())
                    .splineTriggerName(null)
                    .isEquipped(false)
                    .build();
        }

        // 같은 타입 기존 장착 아이템 해제
        inventoryRepository.findEquippedItemByType(
                inventory.getChild().getId(),
                inventory.getItem().getType()
        ).ifPresent(Inventory::unequip);

        // 새 아이템 장착
        inventory.equip();

        return InventoryDto.EquipResponse.builder()
                .inventoryId(inventory.getId())
                .itemType(inventory.getItem().getType())
                .splineTriggerName(inventory.getItem().getSplineTriggerName())
                .isEquipped(true)
                .build();
    }
}