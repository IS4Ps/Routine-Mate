package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.FamilyRelations;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.FamilyRelationsRepository;
import com.hansung.adhd.repository.ParentsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 마법의 단어: 테스트가 끝나면 DB에 넣었던 가짜 데이터를 없던 일로(롤백) 해줍니다!
class FamilyRelationsServiceTest {

    @Autowired FamilyRelationsService familyRelationsService;
    @Autowired ParentsRepository parentsRepository;
    @Autowired ChildrenRepository childrenRepository;
    @Autowired FamilyRelationsRepository familyRelationsRepository;

    @Test
    @DisplayName("부모와 자녀가 가족으로 정상적으로 연결되어야 한다")
    void connectParentAndChildTest() {
        // 1. 가짜 부모님 회원가입 (DB 컬럼 조건에 맞춰 필수값 입력)
        Parents dummyParent = Parents.builder()
                .email("mom@test.com")
                .password("1234")
                .build();
        Parents savedParent = parentsRepository.save(dummyParent);

        // 2. 가짜 아이 가입
        Children dummyChild = Children.builder()
                .nickname("캡스톤아이")
                .build();
        Children savedChild = childrenRepository.save(dummyChild);

        // 3. 방금 만든 우리의 핵심 비즈니스 로직 실행! (엄마로 연결)
        familyRelationsService.connectParentAndChild(
                savedParent.getId(),
                savedChild.getId(),
                "MOM",
                "FULL_ACCESS"
        );

        // 4. 진짜로 DB 중간 테이블에 가족 관계가 잘 들어갔는지 확인
        List<FamilyRelations> relations = familyRelationsRepository.findAll();

        // 검증 1: 관계 데이터가 정확히 1개 생겼는가?
        assertThat(relations.size()).isEqualTo(1);
        // 검증 2: 그 관계의 종류가 "MOM"이 맞는가?
        assertThat(relations.get(0).getRelationType()).isEqualTo("MOM");
        // 검증 3: 연결된 아이의 닉네임이 "캡스톤아이"가 맞는가?
        assertThat(relations.get(0).getChild().getNickname()).isEqualTo("캡스톤아이");

        System.out.println("✅ 테스트 완벽하게 성공! DB에 'MOM'과 '캡스톤아이'가 연결되었습니다.");
    }
}