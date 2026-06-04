package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.DailyMissions;
import com.hansung.adhd.domain.Notifications;
import com.hansung.adhd.domain.Parents;
import com.hansung.adhd.dto.NotificationDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.NotificationsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationsRepository notificationsRepository;

    // 알림 저장 (미션 완료 시 호출)
    @Transactional
    public void saveNotification(Parents parent, Children child, DailyMissions mission,
                                 String title, String body) {
        Notifications notification = Notifications.create(
                parent, child, mission, title, body, "MISSION_COMPLETE"
        );
        notificationsRepository.save(notification);
    }

    // 부모 알림 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getNotifications(Long parentId) {
        return notificationsRepository
                .findByParentIdAndIsDeletedFalseOrderByCreatedAtDesc(parentId)
                .stream()
                .map(NotificationDto.NotificationResponse::from)
                .toList();
    }

    // 읽음 처리
    @Transactional
    public void readNotification(Long notificationId) {
        Notifications notification = notificationsRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.read();
    }
}
