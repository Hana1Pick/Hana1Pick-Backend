package com.hana.hana1pick.domain.common.service;

import com.hana.hana1pick.domain.acchistory.repository.AccHistoryRepository;
import com.hana.hana1pick.domain.acchistory.service.AccHistoryService;
import com.hana.hana1pick.domain.celublog.repository.CelublogRepository;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutHisReqDto;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutReqDto;
import com.hana.hana1pick.domain.common.dto.request.CashOutReqDto;
import com.hana.hana1pick.domain.common.dto.response.*;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.exchange.service.ExchangeFeeService;
import com.hana.hana1pick.domain.exchange.service.ExchangeRateService;
import com.hana.hana1pick.domain.exchange.service.ExchangeService;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.domain.user.service.UserTrsfLimitService;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hana.hana1pick.domain.moaclub.entity.Currency.KRW;
import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountsRepository accountsRepository;
    private final AccHistoryRepository accountHistoryRepository;
    private final DepositRepository depositRepository;
    private final CelublogRepository celublogRepository;
    private final MoaClubRepository moaClubRepository;
    private final AccHistoryService accountHistoryService;
    private final UserTrsfLimitService userTrsfLimitService;
    private final ExchangeService exchangeService; // 환전 서비스
    private ExchangeRateService exchangeRateService;  // 환율 서비스
    private ExchangeFeeService exchangeFeeService;  // 수수료 서비스

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

    public BaseResponse.SuccessResult cashOut(CashOutReqDto request) {
        UUID userIdx = request.getUserIdx();
        String outAccId = request.getOutAccId();
        String inAccId = request.getInAccId();
        Long amount = request.getAmount();

        log.info("Starting cashOut process: userIdx={}, outAccId={}, inAccId={}, amount={}", userIdx, outAccId, inAccId, amount);

        // 1. 입출금 계좌의 유효성 검사
        try {
            handleAccStatus(outAccId);
            handleAccStatus(inAccId);
        } catch (Exception e) {
            log.error("Account status handling failed", e);
            throw new BaseException(ACCOUNT_CASH_OUT_FAIL);
        }

        // 2. 출금 계좌 소유자의 회원 이체한도 초과 확인
        if(!outAccId.substring(3, 5).equals("02") && userTrsfLimitService.checkTrsfLimit(userIdx, amount)){
            throw new BaseException(USER_TRSF_LIMIT_OVER);
        };

        // 3. 입출금 및 입출금 로그 생성
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 3-1. 입출금
            boolean isMoaClubTransaction = getAccountTypeByAccId(outAccId).equals("moaclub") || getAccountTypeByAccId(inAccId).equals("moaclub");
            boolean isForeignCurrency = !request.getCurrency().equals(Currency.KRW);

            log.info("isMoaClubTransaction={}, isForeignCurrency={}", isMoaClubTransaction, isForeignCurrency);

            // 입,출금 계좌 구분
            AccountHistoryInfoDto outAcc;
            AccountHistoryInfoDto inAcc;

            //  모아클럽이면서 원화가 아닌 경우
            if (isMoaClubTransaction && isForeignCurrency) {
                // 환율 및 수수료 적용
                long foreignAmountWithFee = exchangeService.calculateExchangeAmount(request.getCurrency().name(), amount);

                log.info("Calculated foreignAmountWithFee={}", foreignAmountWithFee);

                // 원화로 출금하고 외화로 입금

                //  outAccId가 모아 클럽 계좌이면 환전 금액(foreignAmountWithFee)으로 입금
                if (getAccountTypeByAccId(outAccId).equals("moaclub")) {
                    outAcc = handleAccBalance(outAccId, -amount);
                    inAcc = handleAccBalance(inAccId, foreignAmountWithFee);
                } else { // 일반 -> 모아
                    outAcc = handleAccBalance(outAccId, -foreignAmountWithFee);
                    inAcc = handleAccBalance(inAccId, amount);
                }

                // 거래 로그 생성
                createAccHis(request, outAccId, inAccId, outAcc, inAcc, foreignAmountWithFee);

            } else {
                // 일반 거래
                outAcc = handleAccBalance(outAccId, -amount);
                inAcc = handleAccBalance(inAccId, amount);

                createAccHis(request, outAccId, inAccId, outAcc, inAcc, amount);
            }

            transactionManager.commit(status);
            log.info("Transaction committed successfully");

            // 3-3. 사용자 누적 금액 수정
            if (!getAccountTypeByAccId(outAccId).equals("moaclub")) {
                userTrsfLimitService.accumulate(request.getUserIdx(), amount);
            }
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("Transaction failed and rolled back", e);
            throw new BaseException(ACCOUNT_CASH_OUT_FAIL);
        }

        log.info("cashOut process completed successfully");
        return success(ACCOUNT_CASH_OUT_SUCCESS);
    }


    private void createAccHis(CashOutReqDto request, String outAccId, String inAccId, AccountHistoryInfoDto outAcc, AccountHistoryInfoDto inAcc, Long changeAmount) {
        // 모아클럽 거래이면서 거래 통화가 KRW인 경우 거래내역 2개 생성
        if ((outAccId.substring(3, 5).equals("02") || inAccId.substring(3, 5).equals("02")) && !request.getCurrency().equals(KRW)) {
            // 외화 거래
            accountHistoryService.createAccountHistory(outAcc, inAcc, request.getMemo(), request.getAmount(), request.getTransType(), request.getHashtag(), true);

            // 원화 거래
            accountHistoryService.createAccountHistory(outAcc, inAcc, request.getMemo(), changeAmount, request.getTransType(), request.getHashtag(), false);
        } else {
            accountHistoryService.createAccountHistory(outAcc, inAcc, request.getMemo(), request.getAmount(), request.getTransType(), request.getHashtag(), false);
        }
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
