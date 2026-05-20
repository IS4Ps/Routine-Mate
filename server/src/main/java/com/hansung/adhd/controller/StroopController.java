package com.hansung.adhd.controller;

import com.hansung.adhd.dto.MinigameDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.StroopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Minigames", description = "미니게임 API")
@RestController
@RequestMapping("/api/v1/minigames")
@RequiredArgsConstructor
public class StroopController {

    private final StroopService stroopService;

    @Operation(summary = "스트룹 게임 시작", description = "글자/색상 불일치 문제 세트를 생성합니다.")
    @PostMapping("/stroop/start")
    public ResponseEntity<ApiResponse<MinigameDto.StroopStartResponse>> startStroop(
            @Valid @RequestBody MinigameDto.StroopStartRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(stroopService.startGame(request)));
    }

    @Operation(summary = "스트룹 정답 제출", description = "아이의 색상 선택을 채점하고 결과와 보상을 반환합니다.")
    @PostMapping("/stroop/submit")
    public ResponseEntity<ApiResponse<MinigameDto.StroopSubmitResponse>> submitStroop(
            @Valid @RequestBody MinigameDto.StroopSubmitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(stroopService.submitGame(request)));
    }
}
