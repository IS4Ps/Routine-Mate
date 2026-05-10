package com.hansung.adhd.controller;

import com.hansung.adhd.dto.response.ParentResponseDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.response.ErrorCode;
import com.hansung.adhd.service.ParentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parents")
@RequiredArgsConstructor
public class ParentsController {

    private final ParentsService parentsService;

    /**
     * [GET] 내 정보 조회 API
     */
    @GetMapping("/me")
    public ApiResponse<ParentResponseDto> getMyInfo(Authentication authentication) {
        // ⭐️ 죽은 토큰이 오면 500 에러 대신 안전하게 튕겨내는 방패!
        if (authentication == null || authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN); // 토큰이 이상하다는 예쁜 에러를 던짐!
        }

        Long parentId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(parentsService.getMyInfo(parentId));
    }

    /**
     * [DELETE] 회원 탈퇴 API
     */
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMyAccount(Authentication authentication) {
        Long parentId = Long.parseLong(authentication.getName());
        parentsService.deleteMyAccount(parentId);
        return ApiResponse.noContent();
    }
}