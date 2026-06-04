package com.hansung.adhd.domain;

import com.hansung.adhd.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Notifications")
public class Notifications extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parents parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Children child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private DailyMissions mission;

    @Column(length = 100)
    private String title;

    @Column(length = 255)
    private String body;

    @Column(length = 50)
    private String type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    public void read() {
        this.isRead = true;
    }

    public static Notifications create(Parents parent, Children child, DailyMissions mission,
                                       String title, String body, String type) {
        Notifications notification = new Notifications();
        notification.parent = parent;
        notification.child = child;
        notification.mission = mission;
        notification.title = title;
        notification.body = body;
        notification.type = type;
        notification.isRead = false;
        return notification;
    }
}
