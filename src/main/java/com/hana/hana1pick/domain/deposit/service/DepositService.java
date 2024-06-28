package com.hana.hana1pick.domain.deposit.service;

import com.hana.hana1pick.domain.deposit.dto.request.DepositCreateReqDto;
import com.hana.hana1pick.domain.deposit.dto.response.DepositCreateResDto;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.hana.hana1pick.domain.common.entity.AccountStatus.ACTIVE;
import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.DEPOSIT_CREATED_SUCCESS;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepositService {

    private final AccIdGenerator accIdGenerator;
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;

    public BaseResponse.SuccessResult<DepositCreateResDto> createDeposit(DepositCreateReqDto request) {
        // email로 User 엔티티를 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Email"));

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
}
