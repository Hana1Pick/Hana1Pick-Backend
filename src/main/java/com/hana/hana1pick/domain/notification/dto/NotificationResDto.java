package com.hana.hana1pick.domain.notification.dto;

import com.hana.hana1pick.domain.notification.entity.Notification;
import com.hana.hana1pick.domain.notification.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class NotificationResDto {

    private Long idx;
    private String content;
    private String url;
    private LocalDateTime createdAt;
    private NotificationType type;

    public static NotificationResDto from(Notification notification) {
        return NotificationResDto.builder()
                .idx(notification.getIdx())
                .content(notification.getContent())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .type(notification.getType())
                .build();
    }
}
