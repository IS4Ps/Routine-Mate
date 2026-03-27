package com.hansung.adhd.service;

import com.hansung.adhd.config.JwtProvider;
import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.domain.RefreshToken;
import com.hansung.adhd.dto.response.TokenResponseDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.ParentsRepository;
import com.hansung.adhd.repository.RefreshTokenRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ChildrenRepository childrenRepository;
    private final JwtProvider jwtProvider;

    // ⭐️ 2단계 추가: 자판기 로직을 위해 부모님 창고와 리프레시 토큰 창고 일꾼 추가
    private final ParentsRepository parentsRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 아이 기기 로그인 비즈니스 로직
     */
    @Transactional(readOnly = true)
    public TokenResponseDto childLogin(String deviceId) {

        log.info("아이 기기 로그인 시도 - deviceId: {}", deviceId);

        // 1. 창고(DB)에서 기기 번호로 아이 계정을 찾는다
        Children child = childrenRepository.findByLastConnectedDeviceId(deviceId)
                .orElseThrow(() -> {
                    log.warn("등록되지 않은 기기 번호입니다. deviceId: {}", deviceId);
                    return new CustomException(ErrorCode.CHILD_NOT_FOUND);
                });

        // 2. JwtProvider야, 출입증 만들어라
        String accessToken = jwtProvider.createAccessToken(child.getId(), "ROLE_CHILD");

        // 아이 기기는 평생 고정이므로 일단 더미 유지 (필요시 나중에 고도화)
        String refreshToken = "dummy_refresh_token_for_now";

        return new TokenResponseDto(accessToken, refreshToken);
    }

    /**
     * ⭐️ [NEW] 리프레시 토큰으로 액세스 토큰 재발급 자판기!
     */
    @Transactional
    public TokenResponseDto refreshAccessToken(String givenRefreshToken) {

        log.info("액세스 토큰 재발급 요청이 들어왔습니다.");

        // 1. DB 창고에 이 교환권(리프레시 토큰)이 진짜 있는지 확인!
        RefreshToken tokenEntity = refreshTokenRepository.findById(givenRefreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 2. 토큰 주인의 이메일로 부모님 객체를 찾아서 PK(id)를 가져온다!
        Parents parent = parentsRepository.findByEmail(tokenEntity.getParentEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        // 3. 부모님 PK와 "ROLE_PARENT" 권한으로 따끈따끈한 새 액세스 토큰 발급!
        String newAccessToken = jwtProvider.createAccessToken(parent.getId(), "ROLE_PARENT");

        log.info("새로운 액세스 토큰 발급 완료 - 부모 ID: {}", parent.getId());

        // 4. 새 토큰과 기존 리프레시 토큰을 담아서 프론트엔드로 배송!
        return new TokenResponseDto(newAccessToken, givenRefreshToken);
    }
}