package com.hansung.adhd.controller;

import com.hansung.adhd.dto.MinigameDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.NBackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Minigames", description = "미니게임 API")
@RestController
@RequestMapping("/api/v1/minigames")
@RequiredArgsConstructor
public class NBackController {

    private final NBackService nBackService;

    @Operation(summary = "N-Back 게임 시작", description = "N-Back 자극 시퀀스를 생성합니다.")
    @PostMapping("/n-back/start")
    public ResponseEntity<ApiResponse<MinigameDto.NBackStartResponse>> startNBack(
            @RequestBody MinigameDto.NBackStartRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(nBackService.startGame(request)));
    }

    @Operation(summary = "N-Back 정답 제출", description = "아이의 판단을 채점하고 결과와 보상을 반환합니다.")
    @PostMapping("/n-back/submit")
    public ResponseEntity<ApiResponse<MinigameDto.NBackSubmitResponse>> submitNBack(
            @RequestBody MinigameDto.NBackSubmitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(nBackService.submitGame(request)));
    }
}
