package com.hansung.adhd.dto;

import com.hansung.adhd.domain.Notifications;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class NotificationDto {

    @Getter
    @Builder
    public static class NotificationResponse {
        private Long          notificationId;
        private Long          missionId;
        private String        childNickname;
        private String        title;
        private String        body;
        private String        type;
        private Boolean       isRead;
        private LocalDateTime createdAt;

        public static NotificationResponse from(Notifications n) {
            return NotificationResponse.builder()
                    .notificationId(n.getId())
                    .missionId(n.getMission() != null ? n.getMission().getId() : null)
                    .childNickname(n.getChild() != null ? n.getChild().getNickname() : null)
                    .title(n.getTitle())
                    .body(n.getBody())
                    .type(n.getType())
                    .isRead(n.getIsRead())
                    .createdAt(n.getCreatedAt())
                    .build();
        }
    }
}
