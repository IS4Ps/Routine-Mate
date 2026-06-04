package com.hansung.adhd.controller;

import com.hansung.adhd.dto.NotificationDto;
import com.hansung.adhd.response.ApiResponse;
import com.hansung.adhd.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notifications", description = "알림 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "부모의 알림 목록을 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto.NotificationResponse>>> getNotifications(
            Authentication authentication) {
        Long parentId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotifications(parentId)));
    }

    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> readNotification(
            @PathVariable Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
