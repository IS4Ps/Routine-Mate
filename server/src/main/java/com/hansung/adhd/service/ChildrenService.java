package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.Jobs;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.dto.request.ChildCreateRequestDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.JobsRepository;
import com.hansung.adhd.repository.ParentsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hansung.adhd.dto.response.ChildResponseDto;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildrenService {

    private final ChildrenRepository childrenRepository;
    private final ParentsRepository parentsRepository;
    private final JobsRepository jobsRepository;
    private final ChildLinkTokenStore childLinkTokenStore;

    // ⭐️ 핵심 로직: 부모님 밑으로 아이 프로필 생성하기
    @Transactional
    public Long createChild(Long parentId, ChildCreateRequestDto dto) {

        // 1. 부모님 찾기!
        Parents parent = parentsRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 부모님입니다. ID: " + parentId));

        // 🚨 기기 번호 중복 체크 로직 삭제!!! (QR 연동할 때만 하면 됨!)

        // 2. 아이 객체 조립! (이름만 넣어서 순수하게 프로필만 생성!)
        Children child = Children.builder()
                .parent(parent)
                .nickname(dto.getNickname())
                // .lastConnectedDeviceId(...) 🚨 삭제!!
                .build();

        // 3. 저장!
        Children savedChild = childrenRepository.save(child);
        log.info("새로운 아이 프로필 생성 완료! 아이 ID: {}, 부모 ID: {}", savedChild.getId(), parentId);

        return savedChild.getId();
    }

    /**
     * QR 연동용 일회용 토큰 발급 (10분 유효)
     */
    public String generateLinkToken(Long childId, Long parentId) {
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        if (!child.getParent().getId().equals(parentId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        String token = UUID.randomUUID().toString();
        childLinkTokenStore.save(token, childId);
        return token;
    }

    /**
     * 아이 기기 번호 갱신 (부모 권한 필수)
     */
    @Transactional
    public void updateChildDevice(Long childId, Long parentId, String newDeviceId) {
        // 1. 창고에서 아이 찾기
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        // 2. 권한 검증: 요청한 부모(parentId)가 이 아이의 호적상 부모가 맞는지 깐깐하게 확인!
        if (!child.getParent().getId().equals(parentId)) {
            throw new CustomException(ErrorCode.FORBIDDEN); // "접근 권한이 없습니다." 에러 쾅!
        }

        // 3. 기기 번호 중복 검사 (다른 아이가 이미 쓰고 있는 번호면 튕겨내기)
        if (childrenRepository.findByLastConnectedDeviceId(newDeviceId).isPresent()) {
            throw new IllegalArgumentException("이미 다른 아이에게 등록된 기기 번호입니다.");
        }

        // 4. ⭐️ 기기 번호 갱신! (JPA의 '더티 체킹' 덕분에 save() 안 해도 DB에 알아서 Update 쿼리가 날아감!)
        child.updateDevice(newDeviceId);
    }

    /**
     * 아이 상세 스탯 및 정보 조회
     */
    @Transactional(readOnly = true)
    public ChildResponseDto getChildInfo(Long childId, Long parentId) {
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        if (!child.getParent().getId().equals(parentId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return new ChildResponseDto(
                child.getId(), child.getNickname(), child.getLevel(),
                child.getCurrentExp(), child.getGold(),
                child.getStatStrength(), child.getStatIntelligence(), child.getStatCreativity()
        );
    }

    /**
     * 아이 닉네임 수정
     */
    @Transactional
    public void updateChildNickname(Long childId, Long parentId, String newNickname) {
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        if (!child.getParent().getId().equals(parentId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        child.updateNickname(newNickname);
    }

    /**
     * 직업 선택
     */
    @Transactional
    public void selectJob(Long childId, Long jobId) {
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));
        Jobs job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        child.selectJob(job);
    }

    /**
     * 아이 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteChild(Long childId, Long parentId) {
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        if (!child.getParent().getId().equals(parentId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        // BaseEntity에 있는 delete() 메서드를 호출하여 is_deleted = true 로 변경!
        child.delete();
    }
}