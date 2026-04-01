package com.hansung.adhd.controller;

import com.hansung.adhd.dto.MoodDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.MoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MoodLogs", description = "기분 기록 API")
@RestController
@RequestMapping("/api/v1/mood-logs")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @Operation(summary = "기분 기록 목록 조회", description = "아이의 기분 기록 목록을 최신순으로 조회합니다.")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<MoodDto.MoodResponse>>> getMoodLogs(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(moodService.getMoodLogs(childId)));
    }

    @Operation(summary = "기분 기록 저장", description = "아이의 오늘 기분을 기록합니다. 같은 날짜는 덮어씁니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MoodDto.MoodResponse>> createMoodLog(
            @RequestBody MoodDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(moodService.createMoodLog(request)));
    }
}
