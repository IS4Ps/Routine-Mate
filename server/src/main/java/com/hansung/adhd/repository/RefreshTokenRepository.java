package com.hansung.adhd.repository;

import com.hansung.adhd.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    // 일단 기본 제공되는 save(), findById()만 써도 충분
}