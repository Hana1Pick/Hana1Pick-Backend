package com.hana.hana1pick.domain.common.service;

import com.hana.hana1pick.domain.acchistory.repository.AccountHistoryRepository;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutHisReqDto;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutReqDto;
import com.hana.hana1pick.domain.common.dto.response.AccountForCashOutResDto;
import com.hana.hana1pick.domain.common.dto.response.AccountForCashOutHisResDto;
import com.hana.hana1pick.domain.common.dto.response.AccountInfoDto;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.ACCOUNT_CASH_OUT_HISTORY_LIST_SUCCESS;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.ACCOUNT_CASH_OUT_LIST_SUCCESS;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountsRepository accountsRepository;
    private final AccountHistoryRepository accountHistoryRepository;

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
