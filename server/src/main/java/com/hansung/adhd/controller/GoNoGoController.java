package com.hansung.adhd.controller;

import com.hansung.adhd.dto.MinigameDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.GoNoGoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Minigames", description = "미니게임 API")
@RestController
@RequestMapping("/api/v1/minigames")
@RequiredArgsConstructor
public class GoNoGoController {

    private final GoNoGoService goNoGoService;

    @Operation(summary = "고노고 게임 시작", description = "GO/NOGO 자극 문제 세트를 생성합니다.")
    @PostMapping("/go-no-go/start")
    public ResponseEntity<ApiResponse<MinigameDto.GoNoGoStartResponse>> startGoNoGo(
            @RequestBody MinigameDto.GoNoGoStartRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(goNoGoService.startGame(request)));
    }

    @Operation(summary = "고노고 정답 제출", description = "아이의 답변을 채점하고 결과와 보상을 반환합니다.")
    @PostMapping("/go-no-go/submit")
    public ResponseEntity<ApiResponse<MinigameDto.GoNoGoSubmitResponse>> submitGoNoGo(
            @RequestBody MinigameDto.GoNoGoSubmitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(goNoGoService.submitGame(request)));
    }
}
