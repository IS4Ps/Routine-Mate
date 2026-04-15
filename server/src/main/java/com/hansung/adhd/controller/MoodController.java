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

@Tag(name = "MoodLogs", description = "감정 기록 API")
@RestController
@RequestMapping("/api/v1/mood-logs")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @Operation(summary = "감정 기록 목록 조회", description = "아이의 감정 기록 목록을 최신순으로 조회합니다.")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<MoodDto.MoodResponse>>> getMoodLogs(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(moodService.getMoodLogs(childId)));
    }

    @Operation(summary = "오늘 감정 조회",
            description = "오늘 감정 기록이 있으면 반환, 없으면 null 반환. Flutter가 null이면 감정 선택 화면 표시.")
    @GetMapping("/{childId}/today")
    public ResponseEntity<ApiResponse<MoodDto.MoodResponse>> getTodayMoodLog(
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(moodService.getTodayMoodLog(childId)));
    }

    @Operation(summary = "월간 감정 캘린더 조회",
            description = "해당 월의 날짜별 감정 기록과 평균 점수를 조회합니다.")
    @GetMapping("/{childId}/monthly")
    public ResponseEntity<ApiResponse<MoodDto.MonthlyMoodResponse>> getMonthlyMoodLogs(
            @PathVariable Long childId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(moodService.getMonthlyMoodLogs(childId, year, month)));
    }

    @Operation(summary = "감정 기록 저장", description = "아이의 감정을 기록합니다. 같은 날짜는 덮어씁니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MoodDto.MoodResponse>> createMoodLog(
            @RequestBody MoodDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(moodService.createMoodLog(request)));
    }
}