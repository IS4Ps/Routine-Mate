package com.hansung.adhd.controller;

import com.hansung.adhd.dto.JobResponseDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Jobs", description = "직업 관련 API")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "직업 전체 목록 조회", description = "아이 계정 초기 설정 시와 진행 도중 직업 선택 화면에서 사용합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponseDto>>> getJobList() {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getAllJobs()));
    }
}