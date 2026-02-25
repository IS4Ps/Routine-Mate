package com.hansung.adhd.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Children")
public class Children {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Jobs job;

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

    private Long version = 1L;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}