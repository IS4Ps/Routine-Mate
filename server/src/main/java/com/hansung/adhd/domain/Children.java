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

    // ⭐️ [NEW] 1. 골드 획득 및 사용 로직
    public void addGold(Integer amount) {
        this.gold += amount;
    }

    public void useGold(Integer amount) {
        // 골드가 모자란데 아이템을 사려고 하면 에러를 던져서 막기
        if (this.gold < amount) {
            // TODO: A 머지 후 자네 팀이 만들어둔 CustomException(ErrorCode.INSUFFICIENT_GOLD) 로 교체하게나!
            throw new IllegalArgumentException("골드가 부족합니다.");
        }
        this.gold -= amount;
    }

    // ⭐️ [NEW] 2. 경험치 획득 및 레벨업 로직
    public void gainExp(Integer exp) {
        this.currentExp += exp;
        checkLevelUp(); // 경험치를 얻을 때마다 레벨업 조건이 됐는지 깐깐하게 검사
    }

    // 레벨업 검사기 (외부에서 함부로 호출 못 하게 private으로 숨김!)
    private void checkLevelUp() {
        // [임시 기획] 다음 레벨로 가기 위한 필요 경험치 = 현재 레벨 * 100
        // (예: 1렙->2렙은 100, 2렙->3렙은 200. 나중에 상의해서 바꾸기)
        int requiredExp = this.level * 100;

        // if가 아니라 while을 쓰는 이유
        // 미션 보상을 한 번에 엄청 많이 받아서 2~3레벨이 동시에 오를 수도 있기 때문
        while (this.currentExp >= requiredExp) {
            this.currentExp -= requiredExp; // 필요 경험치만큼 깎고
            this.level++;                   // 레벨 1 증가!

            // 레벨업 했으니, '다음 레벨업'에 필요한 경험치 기준으로 갱신
            requiredExp = this.level * 100;
        }

    }
    public void gainStatStrength(Integer amount) {
        this.statStrength += amount;
    }

    public void gainStatIntelligence(Integer amount) {
        this.statIntelligence += amount;
    }

    public void gainStatCreativity(Integer amount) {
        this.statCreativity += amount;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}