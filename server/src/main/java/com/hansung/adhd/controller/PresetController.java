package com.hansung.adhd.controller;

import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.dto.PresetDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.PresetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Presets", description = "루틴 프리셋 API")
@RestController
@RequestMapping("/api/v1/presets")
@RequiredArgsConstructor
public class PresetController {

    private final PresetService presetService;

    @Operation(summary = "프리셋 목록 조회", description = "부모 계정의 루틴 프리셋 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PresetDto.PresetResponse>>> getPresets(
            @RequestParam Long parentId) {
        return ResponseEntity.ok(ApiResponse.ok(presetService.getPresets(parentId)));
    }

    @Operation(summary = "프리셋 세부 조회", description = "프리셋의 BigTask와 SmallTask 목록을 조회합니다.")
    @GetMapping("/{presetId}")
    public ResponseEntity<ApiResponse<List<PresetDto.BigTaskResponse>>> getPresetDetail(
            @PathVariable Long presetId) {
        return ResponseEntity.ok(ApiResponse.ok(presetService.getPresetDetail(presetId)));
    }

    @Operation(summary = "프리셋 생성", description = "기존 BigTask들을 묶어서 프리셋을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PresetDto.PresetResponse>> createPreset(
            @RequestBody PresetDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(presetService.createPreset(request)));
    }

    @Operation(summary = "프리셋 수정", description = "프리셋 기본 정보를 수정합니다.")
    @PutMapping("/{presetId}")
    public ResponseEntity<ApiResponse<PresetDto.PresetResponse>> updatePreset(
            @PathVariable Long presetId,
            @RequestBody PresetDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(presetService.updatePreset(presetId, request)));
    }

    @Operation(summary = "프리셋 삭제", description = "프리셋을 삭제합니다. (소프트 삭제)")
    @DeleteMapping("/{presetId}")
    public ResponseEntity<ApiResponse<Void>> deletePreset(
            @PathVariable Long presetId) {
        presetService.deletePreset(presetId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "프리셋 불러오기",
               description = "프리셋의 BigTask들을 선택한 날짜부터 DailyMissions로 일괄 생성합니다.")
    @PostMapping("/{presetId}/load")
    public ResponseEntity<ApiResponse<List<MissionDto.MissionResponse>>> loadPreset(
            @PathVariable Long presetId,
            @RequestBody PresetDto.LoadRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(presetService.loadPreset(presetId, request)));
    }
}
