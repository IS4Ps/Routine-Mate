package com.hansung.adhd.controller;

import com.hansung.adhd.dto.BigTaskDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.BigTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BigTasks", description = "루틴 BigTask API")
@RestController
@RequestMapping("/api/v1/big-tasks")
@RequiredArgsConstructor
public class BigTaskController {

    private final BigTaskService bigTaskService;

    @Operation(summary = "BigTask 목록 조회", description = "부모의 BigTask 목록을 SmallTask 포함해서 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BigTaskDto.BigTaskResponse>>> getBigTasks(
            @RequestParam Long parentId) {
        return ResponseEntity.ok(ApiResponse.ok(bigTaskService.getBigTasks(parentId)));
    }

    @Operation(summary = "BigTask 생성", description = "BigTask와 SmallTask를 함께 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<BigTaskDto.BigTaskResponse>> createBigTask(
            @RequestBody BigTaskDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(bigTaskService.createBigTask(request)));
    }

    @Operation(summary = "BigTask 수정", description = "BigTask 정보를 수정합니다.")
    @PutMapping("/{bigTaskId}")
    public ResponseEntity<ApiResponse<BigTaskDto.BigTaskResponse>> updateBigTask(
            @PathVariable Long bigTaskId,
            @RequestBody BigTaskDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(bigTaskService.updateBigTask(bigTaskId, request)));
    }

    @Operation(summary = "BigTask 삭제", description = "BigTask를 삭제합니다. (소프트 삭제)")
    @DeleteMapping("/{bigTaskId}")
    public ResponseEntity<ApiResponse<Void>> deleteBigTask(
            @PathVariable Long bigTaskId) {
        bigTaskService.deleteBigTask(bigTaskId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "BigTask를 프리셋에 묶기", description = "BigTask를 프리셋에 추가합니다.")
    @PatchMapping("/{bigTaskId}/assign-preset/{presetId}")
    public ResponseEntity<ApiResponse<BigTaskDto.BigTaskResponse>> assignToPreset(
            @PathVariable Long bigTaskId,
            @PathVariable Long presetId) {
        return ResponseEntity.ok(ApiResponse.ok(bigTaskService.assignToPreset(bigTaskId, presetId)));
    }
}