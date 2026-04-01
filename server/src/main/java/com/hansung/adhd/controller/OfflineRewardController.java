package com.hansung.adhd.controller;

import com.hansung.adhd.dto.OfflineRewardDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.OfflineRewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "OfflineRewards", description = "오프라인 보상 API")
@RestController
@RequestMapping("/api/v1/offline-rewards")
@RequiredArgsConstructor
public class OfflineRewardController {

    private final OfflineRewardService offlineRewardService;

    @Operation(summary = "오프라인 보상 목록 조회", description = "아이의 오프라인 보상 목록을 조회합니다.")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<OfflineRewardDto.RewardResponse>>> getRewards(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(offlineRewardService.getRewards(childId)));
    }

    @Operation(summary = "오프라인 보상 등록", description = "부모가 아이의 오프라인 보상을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OfflineRewardDto.RewardResponse>> createReward(
            @RequestBody OfflineRewardDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(offlineRewardService.createReward(request)));
    }

    @Operation(summary = "보상 완료 처리", description = "부모가 보상을 지급한 후 완료 처리합니다.")
    @PatchMapping("/{rewardId}/complete")
    public ResponseEntity<ApiResponse<OfflineRewardDto.RewardResponse>> completeReward(
            @PathVariable Long rewardId) {
        return ResponseEntity.ok(ApiResponse.ok(offlineRewardService.completeReward(rewardId)));
    }
}
