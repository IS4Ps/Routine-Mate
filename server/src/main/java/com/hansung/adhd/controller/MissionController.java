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

@Tag(name = "Missions", description = "일일 미션 API")
@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "오늘의 미션 목록 조회", description = "아이의 오늘 미션 목록을 조회합니다.")
    @GetMapping("/today/{childId}")
    public ResponseEntity<ApiResponse<List<MissionDto.MissionResponse>>> getTodayMissions(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getTodayMissions(childId)));
    }

    @Operation(summary = "미션 생성", description = "BigTask를 선택하면 미션이 생성됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> createMission(
            @RequestBody MissionDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.createMission(request)));
    }

    @Operation(summary = "미션 시작", description = "아이가 퀘스트 시작 버튼을 눌렀을 때 호출합니다.")
    @PatchMapping("/{missionId}/start")
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> startMission(
            @PathVariable Long missionId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.startMission(missionId)));
    }

    @Operation(summary = "미션 완료 처리", description = "아이가 미션 완료 버튼을 눌렀을 때 호출합니다.")
    @PatchMapping("/{missionId}/complete")
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> completeMission(
            @PathVariable Long missionId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.completeMission(missionId)));
    }

    @Operation(summary = "미션 승인/거절", description = "부모가 완료된 미션을 승인하거나 거절합니다.")
    @PatchMapping("/{missionId}/review")
    public ResponseEntity<ApiResponse<MissionDto.MissionResponse>> reviewMission(
            @PathVariable Long missionId,
            @RequestBody MissionDto.ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.reviewMission(missionId, request)));
    }

    @Operation(summary = "주간 미션 통계 조회", description = "부모님 대시보드용! 최근 7일간 아이의 미션 달성률을 조회합니다.")
    @GetMapping("/{childId}/statistics")
    public ResponseEntity<ApiResponse<MissionDto.StatisticsResponse>> getWeeklyStatistics(
            @PathVariable Long childId) {

        // 통계 데이터를 공통 박스에 예쁘게 담아서 리턴!
        return ResponseEntity.ok(ApiResponse.ok(missionService.getWeeklyStatistics(childId)));
    }
}