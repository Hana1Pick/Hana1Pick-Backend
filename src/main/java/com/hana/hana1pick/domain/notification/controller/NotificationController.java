package com.hana.hana1pick.domain.notification.controller;

import com.hana.hana1pick.domain.notification.dto.NotificationResDto;
import com.hana.hana1pick.domain.notification.service.NotificationService;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/{email}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String email, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(email, lastEventId);
    }

    @Operation(summary = "알림 목록 조회")
    @GetMapping("/{userIdx}")
    public SuccessResult<List<NotificationResDto>> getNotifications(@PathVariable UUID userIdx) {
        return notificationService.getNotifications(userIdx);
    }

    @Operation(summary = "알림 확인")
    @PostMapping("/{notificationIdx}")
    public SuccessResult checkNotification(@PathVariable Long notificationIdx) {
        return notificationService.checkNotification(notificationIdx);
    }

    @Operation(summary = "알림 삭제")
    @DeleteMapping("/{notificationIdx}")
    public SuccessResult deleteNotification(@PathVariable Long notificationIdx) {
        return notificationService.deleteNotification(notificationIdx);
    }
}
