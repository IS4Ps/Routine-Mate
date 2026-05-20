package com.hansung.adhd.controller;

import com.hansung.adhd.dto.AIQuizDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.AIQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AIQuizzes", description = "AI 퀴즈 API")
@RestController
@RequestMapping("/api/v1/ai-quizzes")
@RequiredArgsConstructor
public class AIQuizController {

    private final AIQuizService aiQuizService;

    @Operation(summary = "AI 퀴즈 생성",
               description = "학습지 이미지를 분석해서 OX 퀴즈를 생성합니다. 이미지는 Base64로 인코딩해서 보내주세요.")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<AIQuizDto.GenerateResponse>> generateQuizzes(
            @Valid @RequestBody AIQuizDto.GenerateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(aiQuizService.generateQuizzes(request)));
    }

    @Operation(summary = "퀴즈 목록 조회",
               description = "아이의 퀴즈 목록을 조회합니다. unsolved=true면 안 푼 퀴즈만 조회합니다.")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<AIQuizDto.QuizResponse>>> getQuizzes(
            @PathVariable Long childId,
            @RequestParam(required = false) Boolean unsolved) {
        return ResponseEntity.ok(ApiResponse.ok(aiQuizService.getQuizzes(childId, unsolved)));
    }

    @Operation(summary = "정답 제출",
               description = "퀴즈 정답을 제출합니다. 정답이면 골드가 지급됩니다.")
    @PostMapping("/{quizId}/answer")
    public ResponseEntity<ApiResponse<AIQuizDto.AnswerResponse>> submitAnswer(
            @PathVariable Long quizId,
            @Valid @RequestBody AIQuizDto.AnswerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(aiQuizService.submitAnswer(quizId, request)));
    }
}
