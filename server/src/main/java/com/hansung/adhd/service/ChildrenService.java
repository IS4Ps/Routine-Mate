package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.dto.request.ChildCreateRequestDto;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.ParentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildrenService {

    private final ChildrenRepository childrenRepository;
    private final ParentsRepository parentsRepository;

    // ⭐️ 핵심 로직: 부모님 밑으로 아이 프로필 생성하기
    // ⭐️ 파라미터가 Long parentId 에서 String parentEmail 로 변경!
    @Transactional
    public Long createChild(Long parentId, ChildCreateRequestDto dto) {

        // 1. 이메일 말고, 다시 ID(PK)로 창고에서 부모님을 찾는다!
        Parents parent = parentsRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 부모님입니다. ID: " + parentId));

        // 2. 기기 번호 중복 체크
        if (childrenRepository.findByLastConnectedDeviceId(dto.getLastConnectedDeviceId()).isPresent()) {
            throw new IllegalArgumentException("이미 다른 아이에게 등록된 기기 번호입니다.");
        }

        // 3. 아이 객체 조립!
        Children child = Children.builder()
                .parent(parent)
                .nickname(dto.getNickname())
                .lastConnectedDeviceId(dto.getLastConnectedDeviceId())
                .build();

        // 4. 저장!
        Children savedChild = childrenRepository.save(child);
        log.info("새로운 아이 프로필 생성 완료! 아이 ID: {}, 부모 ID: {}", savedChild.getId(), parentId);

        return savedChild.getId();
    }
}