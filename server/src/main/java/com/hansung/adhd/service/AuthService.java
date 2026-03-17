package com.hansung.adhd.service;

import com.hansung.adhd.config.JwtProvider;
import com.hansung.adhd.domain.Children;
import com.hansung.adhd.dto.response.TokenResponseDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
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

    /**
     * 아이 기기 로그인 비즈니스 로직
     */
    @Transactional(readOnly = true)
    public TokenResponseDto childLogin(String deviceId) {

        log.info("아이 기기 로그인 시도 - deviceId: {}", deviceId);

        // 1. 창고(DB)에서 기기 번호로 아이 계정을 찾는다!
        // 만약 없으면? 우리가 만든 CustomException을 빵! 터뜨린다!
        Children child = childrenRepository.findByLastConnectedDeviceId(deviceId)
                .orElseThrow(() -> {
                    log.warn("등록되지 않은 기기 번호입니다. deviceId: {}", deviceId);
                    // ErrorCode.USER_NOT_FOUND는 자네 팀이 미리 정의해둔 에러 코드를 쓰면 되네!
                    return new CustomException(ErrorCode.CHILD_NOT_FOUND);
                });

        // 2. 오케이, 우리 회원 맞네! 조폐국(JwtProvider)아, 출입증 만들어라!
        // 아이의 고유 ID(PK)와 "ROLE_CHILD" 권한을 담아서 토큰을 찍어낸다.
        String accessToken = jwtProvider.createAccessToken(child.getId(), "ROLE_CHILD");

        // (참고: 리프레시 토큰은 나중에 갱신 로직 짤 때 추가로 만들면 되니 일단 더미로 두겠네)
        String refreshToken = "dummy_refresh_token_for_now";

        // 3. 예쁘게 포장해서 프론트 데스크(Controller)로 던져준다!
        return new TokenResponseDto(accessToken, refreshToken);
    }
}