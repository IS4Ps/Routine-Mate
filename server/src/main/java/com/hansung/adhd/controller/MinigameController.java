package com.hansung.adhd.controller;

import com.hansung.adhd.dto.MinigameDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.MinigameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MinigameLogs", description = "미니게임 기록 API")
@RestController
@RequestMapping("/api/v1/minigame-logs")
@RequiredArgsConstructor
public class MinigameController {

    private final MinigameService minigameService;

    @Operation(summary = "미니게임 기록 조회", description = "아이의 미니게임 기록을 조회합니다. gameType으로 필터링 가능합니다.")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<MinigameDto.MinigameResponse>>> getMinigameLogs(
            @PathVariable Long childId,
            @RequestParam(required = false) String gameType) {
        return ResponseEntity.ok(ApiResponse.ok(minigameService.getMinigameLogs(childId, gameType)));
    }

    @Operation(summary = "미니게임 결과 저장", description = "미니게임 플레이 결과를 저장하고 골드를 지급합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MinigameDto.MinigameResponse>> saveMinigameLog(
            @Valid @RequestBody MinigameDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(minigameService.saveMinigameLog(request)));
    }
}
