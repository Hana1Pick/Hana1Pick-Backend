package com.hana.hana1pick.domain.deposit.service;

import com.hana.hana1pick.domain.deposit.dto.request.DepositCreateReqDto;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepositService {

//    private final AccIdGenerator accIdGenerator;
//    private final DepositRepository depositRepository;
//
//    @Transactional
//    public String createAccount(DepositCreateReqDto depositCreateReqDto){
//        Deposit deposit = depositRepository.save(depositCreateReqDto.to)



//    }
}
