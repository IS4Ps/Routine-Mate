package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Children")
public class Children extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Jobs job;

    // 핵심: 이 아이의 부모님이 누구인지 연결 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parents parent;

    @Column(length = 50)
    private String nickname;

    private Integer level = 1;

    @Column(name = "current_exp")
    private Integer currentExp = 0;

    private Integer gold = 0;

    @Column(name = "stat_strength")
    private Integer statStrength = 0;

    @Column(name = "stat_intelligence")
    private Integer statIntelligence = 0;

    @Column(name = "stat_creativity")
    private Integer statCreativity = 0;

    @Column(name = "last_connected_device_id", length = 100)
    private String lastConnectedDeviceId;

    public void updateDevice(String newDeviceId) {
        this.lastConnectedDeviceId = newDeviceId;
    }
}