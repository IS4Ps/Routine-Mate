package com.hansung.adhd.dto;

import com.hansung.adhd.domain.Inventory;
import lombok.Builder;
import lombok.Getter;

public class InventoryDto {

    /** 인벤토리 아이템 응답 */
    @Getter
    @Builder
    public static class InventoryResponse {
        private Long    inventoryId;
        private Long    itemId;
        private String  itemName;
        private String  itemType;
        private String  splineTriggerName;
        private Boolean isEquipped;

        public static InventoryResponse from(Inventory inventory) {
            return InventoryResponse.builder()
                    .inventoryId(inventory.getId())
                    .itemId(inventory.getItem().getId())
                    .itemName(inventory.getItem().getName())
                    .itemType(inventory.getItem().getType())
                    .splineTriggerName(inventory.getItem().getSplineTriggerName())
                    .isEquipped(inventory.getIsEquipped())
                    .build();
        }
    }

    /** 아이템 구매 응답 */
    @Getter
    @Builder
    public static class PurchaseResponse {
        private Long    inventoryId;
        private Long    itemId;
        private String  itemName;
        private Integer remainingGold; // TODO: A의 Children 완성 후 실제 잔여 골드 반영
    }

    /** 아이템 장착 응답 */
    @Getter
    @Builder
    public static class EquipResponse {
        private Long    inventoryId;
        private String  itemType;
        private String  splineTriggerName; // Flutter Spline 연동용
        private Boolean isEquipped;
    }
}
