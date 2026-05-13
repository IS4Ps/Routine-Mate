package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Parents")
public class Parents extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // ⭐️나중에 구글/카카오 소셜 로그인을 위해
    @Column(length = 50)
    private String provider;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}