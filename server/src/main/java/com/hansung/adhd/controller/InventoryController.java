package com.hansung.adhd.controller;

import com.hansung.adhd.dto.InventoryDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory", description = "인벤토리 API")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "인벤토리 조회", description = "아이가 보유한 아이템 목록을 조회합니다.")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<InventoryDto.InventoryResponse>>> getInventory(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventory(childId)));
    }

    @Operation(summary = "현재 장착 아이템 조회",
            description = "현재 장착 중인 아이템 목록을 조회합니다. 앱 시작 시 캐릭터 외형 복원에 사용합니다.")
    @GetMapping("/{childId}/equipped")
    public ResponseEntity<ApiResponse<List<InventoryDto.InventoryResponse>>> getEquippedItems(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getEquippedItems(childId)));
    }

    @Operation(summary = "아이템 구매", description = "상점에서 아이템을 구매합니다.")
    @PostMapping("/{itemId}/purchase")
    public ResponseEntity<ApiResponse<InventoryDto.PurchaseResponse>> purchaseItem(
            @PathVariable Long itemId,
            @RequestParam Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.purchaseItem(itemId, childId)));
    }

    @Operation(summary = "아이템 장착/해제", description = "아이템을 장착하거나 해제합니다. 같은 부위는 자동으로 교체됩니다.")
    @PatchMapping("/{inventoryId}/equip")
    public ResponseEntity<ApiResponse<InventoryDto.EquipResponse>> equipItem(
            @PathVariable Long inventoryId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.equipItem(inventoryId)));
    }
}