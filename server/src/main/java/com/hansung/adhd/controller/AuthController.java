package com.hansung.adhd.controller;

import com.hansung.adhd.dto.request.ChildLoginRequestDto;
import com.hansung.adhd.dto.response.TokenResponseDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // 진짜 비즈니스 로직을 처리할 서비스 매니저
    private final AuthService authService;

    /**
     * 아이 기기 로그인 API
     * [POST] /auth/child/login
     */
    @PostMapping("/child/login")
    public ApiResponse<TokenResponseDto> childLogin(@RequestBody ChildLoginRequestDto request) {

        // 1. 프론트 데스크 직원은 받은 신청서(deviceId)를 매니저(Service)에게 토스한다!
        TokenResponseDto tokenResponse = authService.childLogin(request.deviceId());

        // 2. 매니저가 성공적으로 출입증을 만들어오면, 예쁜 공통 응답 박스(ApiResponse)에 담아 손님에게 건넨다!
        return ApiResponse.ok(tokenResponse);
    }
}