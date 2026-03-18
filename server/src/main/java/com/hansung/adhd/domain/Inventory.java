package com.hansung.adhd.domain;
import com.hansung.adhd.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Inventory")
public class Inventory extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Items item;

    @Column(name = "is_equipped")
    private Boolean isEquipped = false;

    private Integer quantity = 1;

    public static Inventory create(Long childId, Items item) {
        Inventory inventory = new Inventory();
        // TODO: A 머지 후 Children 객체로 교체
        inventory.item = item;
        inventory.isEquipped = false;
        return inventory;
    }

    public void equip() { this.isEquipped = true; }
    public void unequip() { this.isEquipped = false; }


}