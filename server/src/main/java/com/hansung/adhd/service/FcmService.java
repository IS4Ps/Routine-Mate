package com.hansung.adhd.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    public void sendToParent(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM 토큰이 없어 알림 전송을 건너뜁니다.");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            log.info("Firebase가 비활성화되어 알림 전송을 건너뜁니다.");
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 알림 전송 성공 - messageId: {}", response);
        } catch (Exception e) {
            log.error("FCM 알림 전송 실패: {}", e.getMessage());
        }
    }
}
