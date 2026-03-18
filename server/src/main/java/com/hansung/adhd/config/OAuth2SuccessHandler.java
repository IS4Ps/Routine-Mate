package com.hansung.adhd.config;

import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.repository.ParentsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final ParentsRepository parentsRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 1. 방금 CustomOAuth2UserService에서 무사히 통과한 유저 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. 이메일 추출 (구글/카카오 분기 처리)
        String email = extractEmail(oAuth2User.getAttributes());

        // 3. DB에서 부모님 계정 찾기
        // (요원 클래스에서 무조건 Upsert 했기 때문에 100% 존재함!)
        Parents parent = parentsRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("부모 계정을 찾을 수 없습니다."));

        // 4. 우리 조폐국(JwtProvider)에서 진짜 JWT 토큰 뚝딱! (역할은 ROLE_PARENT)
        String accessToken = jwtProvider.createAccessToken(parent.getId(), "ROLE_PARENT");
        String refreshToken = "dummy_refresh_token_for_now"; // 리프레시는 일단 더미!

        // 5. 프론트엔드(React/Flutter)의 특정 주소로 토큰을 들고 리다이렉트(이동) 시킨다!
        // ⚠️ 주의: 지금은 로컬 테스트용 React 주소(3000)를 넣었지만, 나중엔 배포된 프론트 주소나 앱 스킴으로 바꿔야 함!
        String targetUrl = "http://localhost:3000/oauth2/redirect?accessToken=" + accessToken + "&refreshToken=" + refreshToken;

        log.info("OAuth2 로그인 성공! JWT 발급 완료. 프론트엔드로 리다이렉트 합니다. email: {}", email);

        // 6. 손님, 프론트엔드로 돌아가십쇼!
        response.sendRedirect(targetUrl);
    }

    /**
     * 구글과 카카오의 JSON 구조 차이를 극복하고 이메일을 빼오는 헬퍼 메서드
     */
    private String extractEmail(Map<String, Object> attributes) {
        // 구글인 경우 바로 꺼내짐
        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        }
        // 카카오인 경우 kakao_account 안에 숨어 있음
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        throw new IllegalArgumentException("이메일 정보를 찾을 수 없습니다.");
    }
}