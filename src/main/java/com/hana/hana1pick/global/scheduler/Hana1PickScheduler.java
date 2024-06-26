package com.hana.hana1pick.global.scheduler;

import com.hana.hana1pick.domain.autotranfer.service.AutoTransferService;
import com.hana.hana1pick.domain.user.service.UserTrsfLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Hana1PickScheduler {

    private final UserTrsfLimitService trsfLimitService;
    private final AutoTransferService autoTransferService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void resetDailyUserTrsfLimit() {
        trsfLimitService.resetUserDailyTrsfLimit();
    }

    @Scheduled(cron = "0 0 10 * * ?") // 매일 오전 10시에 실행
    public void autoTransfer() {
        autoTransferService.autoTransfer();
    }
}
