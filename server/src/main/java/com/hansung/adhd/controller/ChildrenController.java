package com.hansung.adhd.controller;

import com.hansung.adhd.dto.request.ChildCreateRequestDto;
import com.hansung.adhd.dto.request.ChildDeviceUpdateRequestDto;
import com.hansung.adhd.dto.response.ChildResponseDto;
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
     * [POST] QR 연동용 일회용 토큰 발급 (10분 유효)
     */
    @PostMapping("/{childId}/link-token")
    public ApiResponse<String> generateLinkToken(
            @PathVariable Long childId,
            Authentication authentication) {

        Long parentId = Long.parseLong(authentication.getName());
        String token = childrenService.generateLinkToken(childId, parentId);
        return ApiResponse.ok(token);
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
    /**
     * [GET] 아이 상세 정보 및 스탯 조회 API
     */
    @GetMapping("/{childId}")
    public ApiResponse<ChildResponseDto> getChildInfo(
            @PathVariable Long childId,
            Authentication authentication) {

        Long parentId = Long.parseLong(authentication.getName()); // 부모님 토큰 확인!
        return ApiResponse.ok(childrenService.getChildInfo(childId, parentId));
    }

    /**
     * [PATCH] 아이 닉네임 수정 API
     */
    @PatchMapping("/{childId}/nickname")
    public ApiResponse<Void> updateChildNickname(
            @PathVariable Long childId,
            @RequestBody ChildNicknameRequestDto requestDto,
            Authentication authentication) {

        Long parentId = Long.parseLong(authentication.getName());
        childrenService.updateChildNickname(childId, parentId, requestDto.nickname());
        return ApiResponse.noContent();
    }

    /**
     * [PATCH] 직업 선택 API
     */
    @PatchMapping("/{childId}/job")
    public ApiResponse<Void> selectJob(
            @PathVariable Long childId,
            @RequestParam Long jobId) {
        childrenService.selectJob(childId, jobId);
        return ApiResponse.noContent();
    }

    /**
     * [DELETE] 아이 계정 삭제 (소프트 삭제) API
     */
    @DeleteMapping("/{childId}")
    public ApiResponse<Void> deleteChild(
            @PathVariable Long childId,
            Authentication authentication) {

        Long parentId = Long.parseLong(authentication.getName());
        childrenService.deleteChild(childId, parentId);
        return ApiResponse.noContent();
    }

    // 프론트에서 {"nickname": "새로운이름"} 형태로 보낼 때 받을 미니 DTO
    public record ChildNicknameRequestDto(String nickname) {}

//    @Autowired
//    private com.hansung.adhd.config.JwtProvider jwtProvider; // (자네 패키지 경로에 맞게 임포트!)
//
//    @PostConstruct
//    public void generateSuperToken() {
//        // 부모 ID "7"번, 권한 "ROLE_PARENT"로 1000% 유효한 토큰 강제 생성!!
//        String superToken = jwtProvider.createAccessToken(7L, "ROLE_PARENT");
//
//        System.out.println("🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥\n");
//        System.out.println(superToken);
//        System.out.println("🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥\n");
//    }
}