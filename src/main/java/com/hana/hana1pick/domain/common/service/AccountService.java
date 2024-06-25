package com.hana.hana1pick.domain.common.service;

import com.hana.hana1pick.domain.acchistory.repository.AccountHistoryRepository;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutReqDto;
import com.hana.hana1pick.domain.common.dto.response.AccountForCashOutResDto;
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
        List<AccountInfoDto> recentAccId = getAccountInfoFromAccountHistory(accountHistoryRepository.findDistinctInAccIdByOutAccIdOrderByTransDateDesc(outAccId));

        return success(ACCOUNT_CASH_OUT_LIST_SUCCESS, new AccountForCashOutResDto(myAccId, recentAccId));
    }

    public List<AccountInfoDto> getAccountInfoFromAccounts(List<Accounts> accounts){
        List<AccountInfoDto> result = new ArrayList<>();

        accounts.stream()
                .map(a -> new AccountInfoDto(a.getAccountId(), a.getAccountType()))
                .forEach(result::add);

        return result;
    }

    public List<AccountInfoDto> getAccountInfoFromAccountHistory(List<String> accountHistories){
        List<AccountInfoDto> result = new ArrayList<>();

        accountHistories.stream()
                .map(inAccId -> {
                    Accounts account = accountsRepository.findByAccountId(inAccId);
                    return new AccountInfoDto(inAccId, account.getName());
                })
                .forEach(result::add);

        return result;
    }
}