package com.hana.hana1pick.domain.celublog.service;

import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import com.hana.hana1pick.domain.celebrity.repository.CelebrityRepository;
import com.hana.hana1pick.domain.celublog.dto.request.AcceReqDto;
import com.hana.hana1pick.domain.celublog.entity.Celublog;
import com.hana.hana1pick.domain.celublog.repository.CelublogRepository;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CelublogService {
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final CelublogRepository celublogRepository;
    private final AccIdGenerator accIdGenerator;
    private final CelebrityRepository celebrityRepository;
    public SuccessResult accedeCelublog(AcceReqDto req){
        // 예외처리
        User user = getUserByIdx(req.getUserIdx());
        openExceptionHandling(req, user);

        Deposit outAcc = getDepositByAccId(req.getOutAccId());
        String accNum = accIdGenerator.generateCelublogAccId();
        Celebrity celebrity = getCelebrityByIdx(req.getCelebrityIdx());

        Celublog celub = Celublog.builder()
                .accountId(accNum)
                .name(req.getName())
                .imgSrc(req.getImgSrc())
                .outAcc(outAcc)
                .celebrity(celebrity)
                .user(user)
                .ruleList(new ArrayList<>()) // 빈 리스트로 초기화
                .build();

        celublogRepository.save(celub);
        return success(CELUBLOG_CREATED_SUCCESS, accNum);
    }

    private Deposit getDepositByAccId(String outAccId){
        return depositRepository.findById(outAccId).orElseThrow(()->new BaseException(DEPOSIT_NOT_FOUND));
    }
    private User getUserByIdx(UUID userIdx) {
        return getUser(userIdx);
    }
    private User getUser(UUID userIdx) {
        return userRepository.findById(userIdx)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }
    private void openExceptionHandling(AcceReqDto request, User user) {
        // 출금계좌가 사용자 소유 계좌가 아닌 경우
        Deposit outAcc = getDepositByAccId(request.getOutAccId());

        if (!user.getDeposit().equals(outAcc)) {
            throw new BaseException(NOT_ACCOUNT_OWNER);
        }

    }
    private Celebrity getCelebrityByIdx(Long celebrityIdx) {
        return celebrityRepository.findById(celebrityIdx).orElseThrow(() -> new BaseException(CELEBRITY_NOT_FOUND));
    }
}
