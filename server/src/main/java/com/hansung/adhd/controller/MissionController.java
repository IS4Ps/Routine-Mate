package com.hansung.adhd.controller;

import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Missions", description = "미션 API")
@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "오늘의 미션 목록 조회")
    @GetMapping("/today/{childId}")
    public ResponseEntity<ApiResponse<List<MissionDto.MissionResponse>>> getTodayMissions(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getTodayMissions(childId)));
    }

    @Operation(summary = "주간 성공률 조회",
            description = "이번 주 일별 달성 현황과 주간 성공률을 조회합니다. 5일 이상 성공 시 보상 지급 대상.")
    @GetMapping("/stats/{childId}")
    public ResponseEntity<ApiResponse<MissionDto.WeeklyStatsResponse>> getWeeklyStats(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getWeeklyStats(childId)));
    }

    @Operation(summary = "미션 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> createMission(
            @RequestBody MissionDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.createMission(request)));
    }

    @Operation(summary = "미션 시작")
    @PatchMapping("/{missionId}/start")
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> startMission(
            @PathVariable Long missionId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.startMission(missionId)));
    }

    @Operation(summary = "미션 완료")
    @PatchMapping("/{missionId}/complete")
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> completeMission(
            @PathVariable Long missionId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.completeMission(missionId)));
    }

    @Operation(summary = "미션 승인/거절")
    @PatchMapping("/{missionId}/review")
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> reviewMission(
            @PathVariable Long missionId,
            @RequestBody MissionDto.ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.reviewMission(missionId, request)));
    }

    @Operation(summary = "미션 삭제")
    @DeleteMapping("/{missionId}")
    public ResponseEntity<ApiResponse<Void>> deleteMission(
            @PathVariable Long missionId) {
        missionService.deleteMission(missionId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}