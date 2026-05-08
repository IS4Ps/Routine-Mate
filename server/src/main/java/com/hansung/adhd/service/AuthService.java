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

            // 6. 찐 리프레시 토큰은 DB에 얌전히 저장 (기존 토큰이 있다면 덮어쓰거나 갱신하는 로직으로 발전시킬 수 있음)
            refreshTokenRepository.save(new RefreshToken(refreshToken, parent.getEmail()));

            // 7. 자네가 쓰던 TokenResponseDto에 예쁘게 포장해서 반환!
            return new TokenResponseDto(accessToken, refreshToken);

        } catch (Exception e) {
            log.error("구글 로그인 처리 중 서버 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new RuntimeException("구글 로그인 처리 중 오류 발생", e);
        }
    }

    /**
     * ⭐️ [NEW] 부모님 모바일 네이티브 카카오 로그인 검증 로직!
     */
    @Transactional
    public TokenResponseDto kakaoLogin(String kakaoAccessToken) {
        try {
            // 1. 카카오 서버에 "이 토큰 진짜야? 유저 정보 좀 줘!" 하고 물어볼 준비
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            // 카카오는 헤더에 "Bearer {토큰}" 형식으로 담아서 보내야 한다네!
            headers.setBearerAuth(kakaoAccessToken);
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            // 2. 카카오 API 문 두드리기! (유저 정보 가져오는 주소)
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // 3. 카카오가 준 응답 까보기
            Map<String, Object> attributes = response.getBody();
            if (attributes == null) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // 4. 이메일 쏙 뽑아내기!
            // (주의: 카카오는 kakao_account라는 껍질 안에 이메일이 숨어있어!)
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount == null || !kakaoAccount.containsKey("email")) {
                log.error("카카오 로그인 필수 동의 항목(이메일) 누락!");
                throw new CustomException(ErrorCode.BAD_REQUEST); // 필요시 커스텀 에러로 변경
            }

            String email = (String) kakaoAccount.get("email");
            log.info("카카오 로그인 검증 성공! 이메일: {}", email);

            // 5. 우리 DB에서 부모님 찾기 (없으면 가입!) - 구글과 완벽하게 동일!
            Parents parent = parentsRepository.findByEmail(email)
                    .orElseGet(() -> {
                        log.info("신규 부모님 계정 가입 진행 (카카오) - email: {}", email);
                        return parentsRepository.save(
                                Parents.builder()
                                        .email(email)
                                        .password("") // 소셜 로그인이므로 비밀번호는 비워둠
                                        .build()
                        );
                    });

            // 6. 우리 서버 전용 JWT 토큰 뚝딱!
            String accessToken = jwtProvider.createAccessToken(parent.getId(), "ROLE_PARENT");
            String refreshToken = jwtProvider.createRefreshToken();

            // 7. 리프레시 토큰 얌전히 저장
            refreshTokenRepository.save(new RefreshToken(refreshToken, parent.getEmail()));

            return new TokenResponseDto(accessToken, refreshToken);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 서버 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 로그인 처리 중 오류 발생", e);
        }
    }
}