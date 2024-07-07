package com.hana.hana1pick.domain.notification.repository;

import com.hana.hana1pick.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdxAndChecked(UUID userIdx, Boolean checked);
}
