package com.hana.hana1pick.domain.common.service;

import com.hana.hana1pick.domain.acchistory.entity.TransType;
import com.hana.hana1pick.domain.acchistory.repository.AccountHistoryRepository;
import com.hana.hana1pick.domain.acchistory.service.AccountHistoryService;
import com.hana.hana1pick.domain.celublog.repository.CelublogRepository;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutHisReqDto;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutReqDto;
import com.hana.hana1pick.domain.common.dto.request.CashOutReqDto;
import com.hana.hana1pick.domain.common.dto.response.*;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountsRepository accountsRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final DepositRepository depositRepository;
    private final CelublogRepository celublogRepository;
    private final MoaClubRepository moaClubRepository;
    private final AccountHistoryService accountHistoryService;

    final PlatformTransactionManager transactionManager;


    public BaseResponse.SuccessResult<AccountForCashOutResDto> getAccountForCashOut(AccountForCashOutReqDto request) {
        UUID userIdx = request.getUserIdx();
        String outAccId = request.getOutAccId();

        List<AccountInfoDto> myAccId = getAccountInfoFromAccounts(accountsRepository.findByUserIdxAndNotOutAccId(userIdx, outAccId));
        List<AccountInfoDto> recentAccId = getAccountInfoFromAccountHistory(accountHistoryRepository.findDistinctInAccIdAndNameByOutAccIdOrderByTransDateDesc(outAccId));

        return success(ACCOUNT_CASH_OUT_LIST_SUCCESS, new AccountForCashOutResDto(myAccId, recentAccId));
    }

    public BaseResponse.SuccessResult<AccountForCashOutHisResDto> getAccountHistoryForCashOut(AccountForCashOutHisReqDto request) {
        String outAccId = request.getOutAccId();
        String query = request.getQuery();

        List<AccountInfoDto> accId = new ArrayList<>();
        if(query != ""){
            accId = getAccountInfoFromAccountHistory(accountHistoryRepository.findDistinctInAccIdAndNameByOutAccIdAndQueryOrderByTransDateDesc(outAccId, query));
        }

        return success(ACCOUNT_CASH_OUT_HISTORY_LIST_SUCCESS, new AccountForCashOutHisResDto(accId));
    }

    public List<AccountInfoDto> getAccountInfoFromAccounts(List<Accounts> accounts){
        List<AccountInfoDto> result = new ArrayList<>();

        accounts.stream()
                .map(a -> new AccountInfoDto(a.getAccountType(), a.getAccountId(), a.getName()))
                .forEach(result::add);

        return result;
    }

    public List<AccountInfoDto> getAccountInfoFromAccountHistory(List<Object[]> accountHistories){
        List<AccountInfoDto> result = new ArrayList<>();

        accountHistories.stream()
                .map(account -> {
                    String inAccId = (String) account[0];
                    String inAccName = (String) account[1];
                    return new AccountInfoDto(getAccountTypeByAccId(inAccId), inAccId, inAccName);
                })
                .forEach(result::add);

        return result;
    }

    public BaseResponse.SuccessResult cashOut(CashOutReqDto request){
        String outAccId = request.getOutAccId();
        String inAccId = request.getInAccId();
        Long amount = request.getAmount();

        // 1. 입출금 계좌의 유효성 검사
        handleAccStatus(outAccId);
        handleAccStatus(inAccId);

        // 2. 입출금 및 입출금 로그 생성
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 2-1. 입출금
            AccountHistoryInfoDto outAcc = handleAccBalance(outAccId, -amount);
            AccountHistoryInfoDto inAcc = handleAccBalance(inAccId, +amount);
            transactionManager.commit(status);

            // 2-2. 입출금 로그 생성
            accountHistoryService.createAccountHistory(outAcc, inAcc, request.getMemo(), amount, TransType.valueOf("DEPOSIT"), request.getHashtag());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new BaseException(ACCOUNT_CASH_OUT_FAIL);
        }

        return success(ACCOUNT_CASH_OUT_SUCCESS);
    }

    public void handleAccStatus(String accId){
        if(accountsRepository.findAccountsByAccountId(accId).getAccountStatus() == 1){
            throw new BaseException(ACCOUNT_STATUS_INVALID);
        };
    }

    public AccountHistoryInfoDto handleAccBalance(String accId, Long amount) {
        Account acc = getAccountByAccId(accId);
        acc.cashOut(amount);

        return AccountHistoryInfoDto.builder()
                .accountId(accId)
                .name(acc.getName())
                .balance(acc.getBalance())
                .build();
    }

    private Account getAccountByAccId(String accId) {
        switch (accId.substring(3, 5)) {
            case "00":
                return depositRepository.findByAccountId(accId);
            case "01":
                return celublogRepository.findByAccountId(accId);
            default: // "02"
                return moaClubRepository.findByAccountId(accId);
        }
    }



    private String getAccountTypeByAccId(String accId) {
        switch (accId.substring(3, 5)) {
            case "00":
                return "deposit";
            case "01":
                return "celublog";
            default: // "02"
                return "moaclub";
        }
    }
}
