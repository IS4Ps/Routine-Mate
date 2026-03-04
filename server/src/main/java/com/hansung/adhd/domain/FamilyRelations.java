package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder // (데이터를 쉽게 조립하게 해줌)
@AllArgsConstructor // (빌더를 쓰기 위한 필수 짝꿍)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Family_Relations")
public class FamilyRelations extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parents parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    @Column(name = "relation_type", length = 20)
    private String relationType;

    @Column(name = "access_level", length = 20)
    private String accessLevel;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;


}