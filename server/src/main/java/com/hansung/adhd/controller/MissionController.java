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

    @Operation(summary = "오늘의 미션 목록 조회", description = "아이의 오늘 할당된 미션 목록을 조회합니다.")
    @GetMapping("/today/{childId}")
    public ResponseEntity<ApiResponse<List<MissionDto.MissionResponse>>> getTodayMissions(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getTodayMissions(childId)));
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
}
