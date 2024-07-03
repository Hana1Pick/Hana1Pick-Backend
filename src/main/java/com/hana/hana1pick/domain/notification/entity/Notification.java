package com.hana.hana1pick.domain.notification.entity;

import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_idx")
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column
    @NotNull
    private String url;

    @Column
    @NotNull
    private String content;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "Boolean default false")
    private Boolean checked = false;
}