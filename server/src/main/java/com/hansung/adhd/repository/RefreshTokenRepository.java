package com.hansung.adhd.repository;

import com.hansung.adhd.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // ⭐️ Optional 대신 List 임포트!

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    // ⭐️ 수정! 하나만 찾는 게 아니라 List로 싹 다 찾아오기!
    List<RefreshToken> findByParentEmail(String parentEmail);

}