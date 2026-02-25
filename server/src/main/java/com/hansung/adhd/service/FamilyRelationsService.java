package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.FamilyRelations;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.FamilyRelationsRepository;
import com.hansung.adhd.repository.ParentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // "나는 스프링의 서비스(비즈니스 로직 담당) 객체야!" 라고 알려주는 필수 어노테이션
@RequiredArgsConstructor // 아래 적어둔 창고 일꾼(Repository)들을 스프링이 알아서 모셔오게 합니다.
public class FamilyRelationsService {

    // 👷‍♂️ 창고 일꾼 3명 대기발령
    private final ParentsRepository parentsRepository;
    private final ChildrenRepository childrenRepository;
    private final FamilyRelationsRepository familyRelationsRepository;

    /**
     * 부모와 자녀를 가족으로 연결해주는 핵심 비즈니스 로직
     */
    @Transactional // 작업 도중 에러가 나면 하던 작업을 전부 취소(롤백)해주는 강력한 안전장치입니다.
    public void connectParentAndChild(Long parentId, Long childId, String relationType, String accessLevel) {

        // 1. 부모 일꾼에게 지시: "DB에서 parentId 가진 부모 데이터 찾아와!" (없으면 에러 던짐)
        Parents parent = parentsRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부모를 찾을 수 없습니다."));

        // 2. 자녀 일꾼에게 지시: "DB에서 childId 가진 자녀 데이터 찾아와!" (없으면 에러 던짐)
        Children child = childrenRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자녀를 찾을 수 없습니다."));

        // 3. 아까 달아둔 @Builder 를 이용해 새로운 '가족 관계' 데이터 예쁘게 조립하기
        FamilyRelations newRelation = FamilyRelations.builder()
                .parent(parent) // 찾아온 부모 객체 쏙 넣기
                .child(child)   // 찾아온 자녀 객체 쏙 넣기
                .relationType(relationType) // 예: "MOM", "DAD"
                .accessLevel(accessLevel)   // 예: "FULL_ACCESS", "READ_ONLY"
                .isPrimary(true)            // 주 양육자 여부 (일단 true로 고정)
                .build();

        // 4. 가족 일꾼에게 지시: "조립 끝났으니까 DB에 예쁘게 저장해!"
        familyRelationsRepository.save(newRelation);
    }
}