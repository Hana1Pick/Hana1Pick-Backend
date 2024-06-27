package com.hana.hana1pick.domain.autotranfer.service;

import com.hana.hana1pick.domain.autotranfer.entity.AutoTransfer;
import com.hana.hana1pick.domain.autotranfer.repository.AutoTransferRepository;
import com.hana.hana1pick.domain.common.dto.request.CashOutReqDto;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.service.AccountService;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.hana.hana1pick.domain.acchistory.entity.TransType.AUTO_TRANSFER;
import static com.hana.hana1pick.domain.common.entity.AccountStatus.INACTIVE;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.AUTO_TRANSFER_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AutoTransferService {

    private final AutoTransferRepository autoTransferRepository;
    private final AccountService accountService;

    public void autoTransfer() {
        // 자동이체 DB Table에서 출금일이 오늘 날짜인 모든 데이터를 조회
        int today = LocalDate.now().getDayOfMonth();
        List<AutoTransfer> autoTransferList = autoTransferRepository.findByAtDate(today);

        for (AutoTransfer autoTransfer : autoTransferList) {
            Account outAcc = autoTransfer.getOutAcc();
            if (outAcc.getStatus() == INACTIVE) continue;

            CashOutReqDto transfer = CashOutReqDto.of(
                    autoTransfer.getId().getOutAccId(),
                    autoTransfer.getId().getInAccId(),
                    autoTransfer.getAmount(),
                    AUTO_TRANSFER
            );

            accountService.cashOut(transfer);
        }
    }

    public void deleteAutoTrsfByInAccId(String inAccId) {
        List<AutoTransfer> autoTransferList = autoTransferRepository.findByInAccId(inAccId);
        for (AutoTransfer autoTransfer : autoTransferList) {
            autoTransferRepository.delete(autoTransfer);
        }
    }

    public void deleteAutoTrsfByInAccIdAndOutAccId(String inAccId, String outAccId) {
        AutoTransfer autoTransfer = autoTransferRepository.findByInAccIdAndOutAccId(inAccId, outAccId)
                        .orElseThrow(() -> new BaseException(AUTO_TRANSFER_NOT_FOUND));
        autoTransferRepository.delete(autoTransfer);
    }

    public void updateAutoTrsfByInAccId(String inAccId, int atDate, Long amount) {
        List<AutoTransfer> autoTransferList = autoTransferRepository.findByInAccId(inAccId);
        for (AutoTransfer autoTransfer : autoTransferList) {
            autoTransferRepository.save(autoTransfer.updateAutoTransfer(atDate, amount));
        }
    }
}
