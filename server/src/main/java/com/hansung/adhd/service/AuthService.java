package com.hansung.adhd.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
import com.hansung.adhd.dto.response.ChildLinkResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ChildrenRepository childrenRepository;
    private final JwtProvider jwtProvider;
    private final ParentsRepository parentsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ChildLinkTokenStore childLinkTokenStore;

    // ⭐️ 환경 변수 세팅해둔 구글 클라이언트 ID를 가져옵니다!
    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

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

    /**
     * QR 스캔 후 아이 기기 등록 + 로그인
     */
    @Transactional
    public ChildLinkResponseDto registerChildByQr(String linkToken, String deviceId) {
        Long childId = childLinkTokenStore.getChildId(linkToken);
        if (childId == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        child.updateDevice(deviceId);
        childLinkTokenStore.remove(linkToken);

        String accessToken = jwtProvider.createAccessToken(child.getId(), "ROLE_CHILD");
        return new ChildLinkResponseDto(accessToken, child.getId(), child.getNickname());
    }

    /**
     * ⭐️ [NEW] 부모님 모바일 네이티브 구글 로그인 검증 로직!
     */
    @Transactional
    public TokenResponseDto googleLogin(String idTokenString) {
        try {
            // 1. 구글 토큰 검증기 조립
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 2. 프론트가 준 토큰 까보기 (유효하지 않으면 null)
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                log.error("유효하지 않은 구글 토큰입니다: {}", idTokenString);
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // 3. 토큰에서 이메일 쏙 뽑아내기
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            log.info("구글 로그인 검증 성공! 이메일: {}", email);

            // 4. 우리 DB에서 부모님 찾기 (없으면 가입!)
            Parents parent = parentsRepository.findByEmail(email)
                    .orElseGet(() -> {
                        log.info("신규 부모님 계정 가입 진행 - email: {}", email);
                        return parentsRepository.save(
                                Parents.builder()
                                        .email(email)
                                        .password("") // 소셜 로그인이므로 비밀번호는 비워둠
                                        .build()
                        );
                    });

            // 5. 우리 서버 전용 JWT 토큰(Access/Refresh) 뚝딱! 발급
            String accessToken = jwtProvider.createAccessToken(parent.getId(), "ROLE_PARENT");
            String refreshToken = jwtProvider.createRefreshToken();

            // 6. ⭐️ [수정된 코드] 좀비 토큰 싹 다 불러서 모조리 척살!
            List<RefreshToken> existingTokens = refreshTokenRepository.findByParentEmail(parent.getEmail());
            if (!existingTokens.isEmpty()) {
                refreshTokenRepository.deleteAll(existingTokens); // 여러 개를 한 번에 싹 다 지움!
            }

            refreshTokenRepository.save(new RefreshToken(refreshToken, parent.getEmail()));

            // 7. 자네가 쓰던 TokenResponseDto에 예쁘게 포장해서 반환!
            return new TokenResponseDto(accessToken, refreshToken);

        } catch (Exception e) {
            log.error("구글 로그인 처리 중 서버 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new RuntimeException("구글 로그인 처리 중 오류 발생", e);
        }
    }

    /**
     * ⭐️ [NEW] 부모님 모바일 네이티브 카카오 로그인 검증 로직! (이메일 누락 방어 완비)
     */
    @Transactional
    public TokenResponseDto kakaoLogin(String kakaoAccessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(kakaoAccessToken);
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> attributes = response.getBody();
            if (attributes == null) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // ⭐️ 1. 이메일 쏙 뽑아내기! (없으면 카카오 ID로 임시 이메일 만들기 작전!)
            String email;
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

            if (kakaoAccount != null && kakaoAccount.get("email") != null) {
                // 이메일 제공에 동의한 착한 유저
                email = (String) kakaoAccount.get("email");
            } else {
                // ⭐️ 이메일 제공을 거부한 유저라면? 카카오의 고유 회원 번호(id)로 가짜 이메일을 만든다 123456@kakao.com
                Long kakaoId = Long.valueOf(String.valueOf(attributes.get("id")));
                email = kakaoId + "@kakao.com";
                log.warn("카카오 이메일 미동의 유저! 임시 이메일 발급: {}", email);
            }

            log.info("카카오 로그인 검증 성공! 이메일: {}", email);

            // 2. 우리 DB에서 부모님 찾기 (없으면 가입!)
            Parents parent = parentsRepository.findByEmail(email)
                    .orElseGet(() -> {
                        log.info("신규 부모님 계정 가입 진행 (카카오) - email: {}", email);
                        return parentsRepository.save(
                                Parents.builder()
                                        .email(email)
                                        .password("")
                                        .build()
                        );
                    });

            // 3. 우리 서버 전용 JWT 토큰 뚝딱!
            String accessToken = jwtProvider.createAccessToken(parent.getId(), "ROLE_PARENT");
            String refreshToken = jwtProvider.createRefreshToken();

            // 4. ⭐️ 기존 토큰이 있으면 지우고, 새 토큰으로 깔끔하게 저장! (DB 중복 방지)
            refreshTokenRepository.findByParentEmail(parent.getEmail())
                    .ifPresent(existingToken -> refreshTokenRepository.delete(existingToken));

            refreshTokenRepository.save(new RefreshToken(refreshToken, parent.getEmail()));

            return new TokenResponseDto(accessToken, refreshToken);

        } catch (CustomException ce) {
            // ⭐️ 우리가 의도적으로 던진 Custom 에러는 500으로 덮지 말고 그대로 던져라
            throw ce;
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 서버 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 로그인 처리 중 오류 발생", e);
        }
    }
}