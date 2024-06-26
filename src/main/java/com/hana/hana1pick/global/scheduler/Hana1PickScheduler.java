package com.hana.hana1pick.global.scheduler;

import com.hana.hana1pick.domain.autotranfer.service.AutoTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Hana1PickScheduler {

    private final AutoTransferService autoTransferService;

    @Scheduled(cron = "0 0 10 * * ?") // 매일 오전 10시에 실행
    public void autoTransfer() {
        autoTransferService.autoTransfer();
    }
}
