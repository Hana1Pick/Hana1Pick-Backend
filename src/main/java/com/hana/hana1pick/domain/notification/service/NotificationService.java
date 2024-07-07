package com.hana.hana1pick.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.hana1pick.domain.notification.dto.NotificationResDto;
import com.hana.hana1pick.domain.notification.entity.Notification;
import com.hana.hana1pick.domain.notification.entity.NotificationType;
import com.hana.hana1pick.domain.notification.repository.NotificationRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(String email, String lastEventId) {
        String emitterId = makeTimeIncludeEmail(email);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503에러 방지를 위한 더미 이벤트 전송
        sendNotification(emitter, emitterId, "EventStream Created. [email = " + email + "]");

        if (!lastEventId.isEmpty()) {
            sendLostData(lastEventId, emitterId, emitter, email);
        }

        return emitter;
    }

    public void send(User user, String content, String url, NotificationType type) {
        Notification notification = notificationRepository.save(createNotification(user, content, url, type));

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserEmail(user.getEmail());

        emitters.forEach((key, emitter) -> {
                emitterRepository.saveEventCache(key, notification);
                sendNotification(emitter, key, NotificationResDto.from(notification));
        });
    }

    public SuccessResult<List<NotificationResDto>> getNotifications(UUID userIdx) {
        List<NotificationResDto> result = new ArrayList<>();

        List<Notification> notificationList = notificationRepository.findAllByUserIdxAndChecked(userIdx, false);

        for (Notification notification : notificationList) {
            result.add(NotificationResDto.from(notification));
        }

        return success(NOTIFICATION_FETCH_SUCCESS, result);
    }

    public SuccessResult checkNotification(Long notificationIdx) {
        Notification notification = getNotificationByIdx(notificationIdx);

        notificationRepository.save(notification.checkNotification(true));
        return success(NOTIFICATION_CHECK_SUCCESS);
    }

    public SuccessResult deleteNotification(Long notificationIdx) {
        Notification notification = getNotificationByIdx(notificationIdx);

        notificationRepository.delete(notification);
        return success(NOTIFICATION_DELETE_SUCCESS);
    }

    private String makeTimeIncludeEmail(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String emitterId, Object data) {
        try {
            String jsonData = new ObjectMapper().writeValueAsString(data);
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(jsonData));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private void sendLostData(String lastEventId, String emitterId, SseEmitter emitter, String email) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserEmail(email);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, emitterId, entry.getValue()));
    }

    private Notification createNotification(User user, String content, String url, NotificationType type) {
        return Notification.builder()
                .user(user)
                .content(content)
                .url(url)
                .checked(false)
                .type(type)
                .build();
    }

    private Notification getNotificationByIdx(Long notificationIdx) {
        return notificationRepository.findById(notificationIdx)
                .orElseThrow(() -> new BaseException(NOTIFICATION_NOT_FOUND));
    }
}
