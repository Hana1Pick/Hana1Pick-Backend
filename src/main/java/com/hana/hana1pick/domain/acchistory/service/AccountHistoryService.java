package com.hana.hana1pick.domain.acchistory.service;

import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.entity.TransType;
import com.hana.hana1pick.domain.acchistory.repository.AccountHistoryRepository;
import com.hana.hana1pick.domain.common.dto.response.AccountHistoryInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryService {
    private final AccountHistoryRepository accountHistoryRepository;

    public void createAccountHistory(AccountHistoryInfoDto outAcc, AccountHistoryInfoDto inAcc, String memo, Long amount, TransType transType, String hashtag) {
        AccountHistory accountHistory = AccountHistory.builder()
                .memo(memo)
                .transDate(LocalDateTime.now())
                .transType(transType)
                .transAmount(amount)
                .inAccId(inAcc.getAccountId())
                .inAccName(inAcc.getName())
                .outAccId(outAcc.getAccountId())
                .beforeInBal(inAcc.getBalance()-amount)
                .afterInBal(inAcc.getBalance())
                .beforeOutBal(outAcc.getBalance()+amount)
                .afterOutBal(outAcc.getBalance())
                .hashtag(hashtag)
                .build();

        accountHistoryRepository.save(accountHistory);
    }
}
