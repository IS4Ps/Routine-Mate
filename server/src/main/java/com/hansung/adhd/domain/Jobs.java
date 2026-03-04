package com.hansung.adhd.domain;

import com.hansung.adhd.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Jobs")
public class Jobs extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @Column(name = "job_code", length = 50)
    private String jobCode;

    @Column(name = "job_name", length = 50)
    private String jobName;

    @Column(length = 255)
    private String description;

    @Column(name = "base_strength")
    private Integer baseStrength;

    @Column(name = "base_intelligence")
    private Integer baseIntelligence;

    @Column(name = "base_creativity")
    private Integer baseCreativity;
}