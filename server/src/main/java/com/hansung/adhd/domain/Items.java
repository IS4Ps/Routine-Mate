package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Items")
public class Items extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_job_id")
    private Jobs requiredJob;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String type;

    private Integer price;

    @Column(name = "spline_trigger_name", length = 100)
    private String splineTriggerName;

    @Column(name = "required_level")
    private Integer requiredLevel;



}