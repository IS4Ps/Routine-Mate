package com.hansung.adhd.controller;

import com.hansung.adhd.dto.request.ChildLoginRequestDto;
import com.hansung.adhd.dto.request.GoogleLoginRequestDto;
import com.hansung.adhd.dto.request.KakaoLoginRequestDto;
import com.hansung.adhd.dto.response.ChildLinkResponseDto;
import com.hansung.adhd.dto.response.TokenResponseDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 아이 기기 로그인 API
     * [POST] /auth/child/login
     */
    @PostMapping("/child/login")
    public ApiResponse<TokenResponseDto> childLogin(@RequestBody ChildLoginRequestDto requestDto) {
        TokenResponseDto tokenResponse = authService.childLogin(requestDto.deviceId());
        return ApiResponse.ok(tokenResponse);
    }

    /**
     * QR 스캔 후 아이 기기 등록 + 로그인
     * [POST] /auth/child/register-by-qr
     */
    @PostMapping("/child/register-by-qr")
    public ApiResponse<ChildLinkResponseDto> registerChildByQr(
            @RequestParam String linkToken,
            @RequestParam String deviceId) {
        return ApiResponse.ok(authService.registerChildByQr(linkToken, deviceId));
    }

    /**
     * ⭐️ [NEW] 액세스 토큰 재발급 (리프레시) API
     * [POST] /auth/token/refresh
     */
    @PostMapping("/token/refresh")
    public ApiResponse<TokenResponseDto> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponseDto newTokens = authService.refreshAccessToken(refreshToken);
        return ApiResponse.ok(newTokens);
    }

    /**
     * 부모님 모바일 네이티브 구글 로그인 API
     * [POST] /auth/google
     */
    @PostMapping("/google")
    public ApiResponse<TokenResponseDto> googleLogin(@RequestBody GoogleLoginRequestDto requestDto) {
        // 1. 프론트가 폰에서 뽑아온 구글 토큰을 매니저에게 토스!
        TokenResponseDto tokenResponse = authService.googleLogin(requestDto.getIdToken());
        // 2. 발급받은 우리 서버 출입증 세트(Access/Refresh)를 예쁘게 포장해서 리턴!
        return ApiResponse.ok(tokenResponse);
    }

    /**
     * ⭐️ [NEW] 부모님 모바일 네이티브 카카오 로그인 API
     * [POST] /auth/kakao
     */
    @PostMapping("/kakao")
    public ApiResponse<TokenResponseDto> kakaoLogin(@RequestBody KakaoLoginRequestDto requestDto) {
        // 1. 프론트가 던져준 카카오 액세스 토큰을 매니저에게 토스!
        TokenResponseDto tokenResponse = authService.kakaoLogin(requestDto.getAccessToken());
        // 2. 발급받은 우리 서버 출입증 세트(Access/Refresh)를 예쁘게 포장해서 리턴!
        return ApiResponse.ok(tokenResponse);
    }
}