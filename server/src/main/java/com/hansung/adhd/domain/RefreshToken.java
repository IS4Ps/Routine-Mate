package com.hansung.adhd.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "refresh_token_id")
    private String token; // 리프레시 토큰 값 자체를 PK로 쓴다네!

    @Column(nullable = false)
    private String parentEmail; // 이 토큰의 주인이 어떤 부모님인지 기억!

    @Builder
    public RefreshToken(String token, String parentEmail) {
        this.token = token;
        this.parentEmail = parentEmail;
    }
}