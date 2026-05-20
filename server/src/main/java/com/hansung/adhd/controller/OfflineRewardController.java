package com.hansung.adhd.controller;

import com.hansung.adhd.dto.OfflineRewardDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.OfflineRewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @Operation(summary = "오프라인 보상 목록 조회")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<OfflineRewardDto.RewardResponse>>> getRewards(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(offlineRewardService.getRewards(childId)));
    }

    @Operation(summary = "오프라인 보상 등록",
            description = "목표 성공 일수와 보상 내용을 설정합니다. targetPercent 생략 시 70%로 설정됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OfflineRewardDto.RewardResponse>> createReward(
            @Valid @RequestBody OfflineRewardDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(offlineRewardService.createReward(request)));
    }

    @Operation(summary = "보상 달성 여부 확인",
            description = "이번 주 목표 달성 여부를 확인합니다. true면 보상 수령 가능.")
    @GetMapping("/{rewardId}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkEligibility(
            @PathVariable Long rewardId) {
        return ResponseEntity.ok(ApiResponse.ok(offlineRewardService.checkRewardEligibility(rewardId)));
    }

    @Operation(summary = "보상 완료 처리",
            description = "부모가 보상 지급 후 완료 처리합니다.")
    @PatchMapping("/{rewardId}/complete")
    public ResponseEntity<ApiResponse<OfflineRewardDto.RewardResponse>> completeReward(
            @PathVariable Long rewardId) {
        return ResponseEntity.ok(ApiResponse.ok(offlineRewardService.completeReward(rewardId)));
    }
}
