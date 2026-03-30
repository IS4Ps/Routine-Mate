package com.hansung.adhd.service;

import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.dto.response.ParentResponseDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ParentsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParentsService {

    private final ParentsRepository parentsRepository;

    @Transactional(readOnly = true)
    public ParentResponseDto getMyInfo(Long parentId) {
        Parents parent = parentsRepository.findById(parentId)
                // TODO: ErrorCode.USER_NOT_FOUND 등 팀 컨벤션에 맞게 수정
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new ParentResponseDto(parent.getId(), parent.getEmail(), parent.getProvider());
    }

    @Transactional
    public void deleteMyAccount(Long parentId) {
        Parents parent = parentsRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        parent.delete(); // 부모 계정 소프트 삭제!
    }
}