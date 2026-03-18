package com.hansung.adhd.controller;

import com.hansung.adhd.dto.ItemDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Items", description = "아이템 상점 API")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "상점 아이템 목록 조회",
               description = "아이 레벨과 직업에 맞는 아이템 목록을 조회합니다. type으로 필터링 가능합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDto.ItemResponse>>> getShopItems(
            @RequestParam(required = false) String type,
            @RequestParam Integer childLevel,
            @RequestParam Long childJobId) {
        return ResponseEntity.ok(ApiResponse.ok(
                itemService.getShopItems(type, childLevel, childJobId)));
    }
}
