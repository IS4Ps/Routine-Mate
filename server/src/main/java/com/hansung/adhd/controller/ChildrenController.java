package com.hansung.adhd.controller;

import com.hansung.adhd.dto.request.ChildCreateRequestDto;
import com.hansung.adhd.service.ChildrenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildrenController {

    private final ChildrenService childrenService;

    @PostMapping
    public ResponseEntity<String> createChild(
            Authentication authentication,
            @RequestBody ChildCreateRequestDto requestDto) {

        // 1. 만능 열쇠로 토큰 안의 핵심 값(Subject)을 꺼낸다!
        String tokenValue = authentication.getName();
        log.info("⭐️ JWT 토큰에서 꺼낸 값: {}", tokenValue); // 콘솔에서 직접 확인해보자!

        // 2. 그 값을 숫자(Long)로 바꿔서 매니저에게 넘긴다!
        Long parentId = Long.parseLong(tokenValue);
        Long childId = childrenService.createChild(parentId, requestDto);

        return ResponseEntity.ok("아이 프로필 생성 성공! 아이 ID: " + childId);
    }
}