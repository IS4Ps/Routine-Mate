package com.hansung.adhd.controller;

import com.hansung.adhd.dto.request.ChildCreateRequestDto;
import com.hansung.adhd.dto.request.ChildDeviceUpdateRequestDto;
import com.hansung.adhd.response.ApiResponse;
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

    /**
     * [PATCH] 아이 기기 번호 갱신 API
     */
    @PatchMapping("/{childId}/device")
    public ApiResponse<Void> updateChildDevice(
            @PathVariable Long childId,
            @RequestBody ChildDeviceUpdateRequestDto requestDto,
            Authentication authentication) {

        log.info("아이 기기 번호 변경 요청 - childId: {}, newDeviceId: {}", childId, requestDto.newDeviceId());

        // 1. 보안 뱃지(토큰)에서 부모 PK(ID) 꺼내기
        Long parentId = Long.parseLong(authentication.getName());

        // 2. 매니저(Service)에게 기기 번호 변경 지시!
        childrenService.updateChildDevice(childId, parentId, requestDto.newDeviceId());

        // 3. 성공 응답 리턴! (돌려줄 데이터가 없으니 성공 코드 201 혹은 200만 쿨하게 던짐)
        return ApiResponse.noContent();
    }
}