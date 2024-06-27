package com.hana.hana1pick.domain.celublog.service;

import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.repository.AccHistoryRepository;
import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import com.hana.hana1pick.domain.celebrity.repository.CelebrityRepository;
import com.hana.hana1pick.domain.celublog.dto.request.AcceReqDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccDetailResDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccDetailResDto.AccReport;
import com.hana.hana1pick.domain.celublog.dto.response.AccListResDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CelublogService {
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final CelublogRepository celublogRepository;
    private final AccIdGenerator accIdGenerator;
    private final CelebrityRepository celebrityRepository;
    private final AccHistoryRepository accountHistoryRepository;
    //선택한 계좌 상세 내용 조회
    public SuccessResult celubAccDetail(String accountId){
        //accountInfo, ruleInfoList celub에 담겨옴
        Celublog celub = celublogRepository.findByAccountId(accountId);
        LocalDateTime today = LocalDateTime.now();
        long duration = ChronoUnit.DAYS.between(celub.getCreateDate(), today);
        AccDetailResDto.AccInfo accInfo = new AccDetailResDto.AccInfo(celub.getBalance(), celub.getName(), celub.getImgSrc(), celub.getOutAcc(), celub.getCelebrity(), duration);
        //계좌 거래 내역
        List<AccountHistory> history = accountHistoryRepository.findByAccountId(accountId);
        List<AccReport> accountReportList = new ArrayList<>();
        for (AccountHistory h : history) {
            AccReport report = new AccReport(h.getTransDate().getMonthValue()+"."+h.getTransDate().getDayOfMonth(), h.getMemo(), h.getTransAmount(), h.getAfterInBal(), h.getHashtag());
            accountReportList.add(report);
        }
        AccDetailResDto dto = new AccDetailResDto(accInfo, celub.getRuleList(), accountReportList);
        return success(CELUBLOG_ACCOUNT_DETAIL_SUCCESS, dto);
    }

    //로그인 한 유저가 갖고 있는 계좌 리스트
    public SuccessResult celubAccList(UUID userIdx){
        List<Celublog> celublogList = celublogRepository.findByUserIdx(userIdx);
        List<AccListResDto> resList = new ArrayList<>();
        for(int i=0; i<celublogList.size(); i++){
            Celublog tmp = celublogList.get(i);
            AccListResDto dto = new AccListResDto(tmp.getName(), tmp.getAccountId());
            resList.add(dto);
        }
        return success(CELUBLOG_ACCOUNT_LIST_SUCCESS, resList);
    }
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
