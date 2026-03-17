package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Parents;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParentsRepository extends JpaRepository<Parents, Long> {

    // 소셜 로그인 시 받아온 이메일로 기존 가입자인지 확인하는 쿼리 메서드!
    Optional<Parents> findByEmail(String email);
}