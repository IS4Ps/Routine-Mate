package com.hansung.adhd.service;
//
//import com.hansung.adhd.domain.Parents;
//import com.hansung.adhd.dto.OAuthAttributes;
//import com.hansung.adhd.repository.ParentsRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collections;
/**
 * 모바일 네이티브 로그인(REST API) 방식으로 아키텍처가 변경되면서 폐기된 클래스입니다!
 * 스프링이 이 객체를 만들지 않도록 @Service 까지 주석 처리했습니다.
 */
@Deprecated
//@Slf4j
//@Service
//@RequiredArgsConstructor
public class CustomOAuth2UserService /*extends DefaultOAuth2UserService*/ {
//
//    private final ParentsRepository parentsRepository;
//
//    @Override
//    @Transactional
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        // 1. 현재 로그인 진행 중인 서비스 구분 코드 (구글인지 카카오인지)
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//
//        // 2. 해당 소셜 플랫폼의 기본 PK 필드 이름 (구글은 "sub", 카카오는 "id")
//        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
//                .getUserInfoEndpoint().getUserNameAttributeName();
//
//        // 3. ★ 번역기 가동! 어떤 플랫폼이든 똑같은 모양의 객체(OAuthAttributes)로 변환해 준다! ★
//        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
//
//        log.info("OAuth2 로그인 성공 - 서비스: {}, 이메일: {}", registrationId, attributes.getEmail());
//
//        // 4. 추출된 이메일로 DB에 Upsert (가입 or 업데이트) 진행
//        Parents parent = saveOrUpdate(attributes.getEmail());
//
//        // 5. 시큐리티 세션에 저장될 객체 반환 (우리가 만든 번역기의 attributes를 넣어줌)
//        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_PARENT")),
//                attributes.getAttributes(),
//                attributes.getNameAttributeKey()
//        );
//    }
//
//    /**
//     * DB에 이메일이 있으면 '최근 로그인 시간'만 업데이트하고,
//     * 없으면 '새로운 부모님 계정'으로 Insert 하는 헬퍼 메서드
//     */
//    private Parents saveOrUpdate(String email) {
//        Parents parent = parentsRepository.findByEmail(email)
//                .map(entity -> {
//                    // 이미 가입된 회원이면 최근 로그인 시간(lastLoginAt)만 갱신! (현재 엔티티에 setter가 없으니 builder로 대체하거나 도메인 메서드 추가 권장)
//                    log.info("기존 부모님 계정 로그인 - email: {}", email);
//                    // TODO: Parents 엔티티에 updateLastLogin() 같은 도메인 메서드를 만들어두면 더 좋네!
//                    return entity;
//                })
//                .orElseGet(() -> {
//                    // 처음 온 회원이면 새로 DB에 저장!
//                    log.info("신규 부모님 계정 가입 - email: {}", email);
//                    return Parents.builder()
//                            .email(email)
//                            // 소셜 로그인은 비밀번호가 필요 없으니 임의의 값이나 빈 문자열 처리
//                            .password("")
//                            .build();
//                });
//
//        return parentsRepository.save(parent);
//    }
}