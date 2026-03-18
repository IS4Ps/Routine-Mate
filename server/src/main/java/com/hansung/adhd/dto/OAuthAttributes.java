package com.hansung.adhd.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 소셜 플랫폼마다 다른 JSON 구조를 하나로 통일해주는 마법의 번역기
 */
@Getter
@Builder
public class OAuthAttributes {

    private Map<String, Object> attributes; // 소셜 플랫폼이 던져준 원본 JSON 데이터
    private String nameAttributeKey;        // 구글의 "sub", 카카오의 "id" 같은 PK 키워드
    private String name;                    // 유저 이름 (또는 닉네임)
    private String email;                   // 유저 이메일

    // 1. 어떤 플랫폼에서 왔는지 확인하고 전용 번역기로 토스하는 분기점
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오 로그인일 경우
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        // 기본은 구글 로그인으로 처리
        return ofGoogle(userNameAttributeName, attributes);
    }

    // 2. 구글 전용 번역기 (데이터가 1층에 바로 있음)
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 3. 카카오 전용 번역기 (데이터가 2층, 3층에 숨어 있음!)
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // kakao_account(2층) 안으로 들어간다
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        // 그 안의 profile(3층) 안으로 또 들어간다
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname")) // 3층에서 닉네임 꺼내기
                .email((String) kakaoAccount.get("email"))   // 2층에서 이메일 꺼내기
                .attributes(attributes)                      // 원본 데이터는 그대로 보존
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}