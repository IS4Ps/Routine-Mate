package com.hansung.adhd.controller;

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
}
