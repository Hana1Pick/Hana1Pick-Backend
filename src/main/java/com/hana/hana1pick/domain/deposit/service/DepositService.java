package com.hana.hana1pick.domain.deposit.service;

import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.domain.deposit.dto.request.DepositAccIdReqDto;
import com.hana.hana1pick.domain.deposit.dto.request.DepositCreateReqDto;
import com.hana.hana1pick.domain.deposit.dto.response.DepositCreateResDto;
import com.hana.hana1pick.domain.deposit.dto.response.DepositDetailResDto;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.hana.hana1pick.domain.common.entity.AccountStatus.ACTIVE;
import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepositService {

    private final AccIdGenerator accIdGenerator;
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final AccountsRepository accountsRepository;


    // 사용자 정보 수정(회원가입)
    public BaseResponse.SuccessResult<DepositCreateResDto> createDeposit(DepositCreateReqDto request) {
        // email로 User 엔티티를 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        String accId = getAccId();

        Deposit deposit = createDeposit(request, accId, user);

        // Deposit 엔티티를 저장
        depositRepository.save(deposit);

        return success(DEPOSIT_CREATED_SUCCESS, new DepositCreateResDto(accId));
    }

    private String getAccId() {
        String accId;
        do {
            accId = accIdGenerator.generateDepositAccId();
        } while (depositRepository.existsById(accId));

        return accId;
    }

    private Deposit createDeposit(DepositCreateReqDto request, String accId, User user) {
        return Deposit.builder()
                .balance(0L)
                .status(ACTIVE)
                .accountId(accId)
                .name(request.getName())
                .user(user)
                .build();
    }

    // 계좌 상세 조회
    public BaseResponse.SuccessResult<DepositDetailResDto> getDepositDetail(DepositAccIdReqDto request) {

        validateAccount(request.getAccountId());

        Deposit deposit = getDepositByAccId(request.getAccountId());

        return success(DEPOSIT_FETCH_SUCCESS, DepositDetailResDto.of(deposit));
    }

    private Deposit getDepositByAccId(String accId) {
        return depositRepository.findById(accId)
                .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
    }

    private void validateAccount(String accountId) {
        // 계좌가 존재하는지 확인
        Accounts account = getAccByAccId(accountId);

        // 해지된 계좌인지 확인
        AccountStatus status = AccountStatus.fromCode(account.getAccountStatus());
        if (status == AccountStatus.INACTIVE) {
            throw new BaseException(ACCOUNT_INACTIVE);
        }
    }

    private Accounts getAccByAccId(String accountId) {
        return accountsRepository.findById(accountId)
                .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
    }
}
