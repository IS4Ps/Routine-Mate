package com.hansung.adhd.repository;

import com.hansung.adhd.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // ⭐️ Optional 임포트 까먹지 말고!

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    // ⭐️ 부모 이메일로 리프레시 토큰 찾기!
    Optional<RefreshToken> findByParentEmail(String parentEmail);

}