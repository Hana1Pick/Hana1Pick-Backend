package com.hana.hana1pick.domain.celublog.service;

import com.hana.hana1pick.domain.acchistory.dto.request.AccHistoryReqDto;
import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryResDto;
import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.repository.AccHistoryRepository;
import com.hana.hana1pick.domain.acchistory.service.AccHistoryService;
import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import com.hana.hana1pick.domain.celebrity.repository.CelebrityRepository;
import com.hana.hana1pick.domain.celublog.dto.request.*;
import com.hana.hana1pick.domain.celublog.dto.response.AccDetailResDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccDetailResDto.AccReport;
import com.hana.hana1pick.domain.celublog.dto.response.AccListResDto;
import com.hana.hana1pick.domain.celublog.dto.response.CelubListDto;
import com.hana.hana1pick.domain.celublog.entity.Celublog;
import com.hana.hana1pick.domain.celublog.entity.Rules;
import com.hana.hana1pick.domain.celublog.repository.CelublogRepository;
import com.hana.hana1pick.domain.celublog.repository.RulesRepository;
import com.hana.hana1pick.domain.common.dto.request.CashOutReqDto;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.common.service.AccountService;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.config.S3Service;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hana.hana1pick.domain.acchistory.entity.TransType.DEPOSIT;
import static com.hana.hana1pick.domain.acchistory.entity.TransType.WITHDRAW;
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
    private final RulesRepository rulesRepository;
    private final AccountService accountService;
    private final S3Service s3Service;
    private final AccHistoryService accHistoryService;
    //셀럽로그 배경, 이름 변경
    public SuccessResult celubModifyInfo(AlterationReqDto req){
        Celublog celub = getCelubByAccId(req.getAccountId());

        if(celub.equals(null)) new BaseException(CELEBRITY_NOT_FOUND_ACCOUNT);
        if(req.getField().equals("name")){
            celublogRepository.updateName(req.getAccountId(), req.getName());
        }else if(req.getField().equals("imgSrc")){
            String saveUrl = s3Service.uploadPng(req.getSrcImg(),"celubBgImg");
            celublogRepository.updateImgSrc(req.getAccountId(), saveUrl);
        }
        //else rule
        return success(CELUBLOG_MODIFY_CELUBLIST_SUCCESS);
    }
    //연예인 검색
    public SuccessResult celubSearchList(SearchReqDto req) {
        List<Celebrity> celebrityList = celebrityRepository.findByKeyword(req.getUserIdx(), req.getType(), req.getName());

        List<CelubListDto> celubList = new ArrayList<>();
        celebrityList.forEach(item->{
            CelubListDto dto = CelubListDto.of(item.getType(), item.getIdx(), item.getName(), item.getThumbnail());
            celubList.add(dto);
        });

        return success(CELUBLOG_SEARCH_CELUBLIST_SUCCESS, celubList);
    }

    //생성 가능한 연예인 리스트
    public SuccessResult celubList(UUID userIdx){
        // 개설하지 않은 연예인 리스트
        List<Long> celubIdxList = celublogRepository.findClubNumByUserIdx(userIdx);
        List<CelubListDto> celubList = new ArrayList<>();
        for(Long idx:celubIdxList){
            Optional<Celebrity> celub = celebrityRepository.findById(idx);
            CelubListDto dto = CelubListDto.of(celub.get().getType(), idx, celub.get().getName(), celub.get().getThumbnail());
            celubList.add(dto);
        }
        return success(CELUBLOG_CELUBLIST_SUCCESS, celubList);
    }

    //룰에 따른 입금
    public SuccessResult celubAddIn(AccInReqDto req){
        //셀럽로그 계좌번호로 출금 계좌번호 찾아오기
        Celublog celub = celublogRepository.findByAccountId(req.getAccountId());
        Deposit tmpAccount = celub.getOutAcc();
        //출금 계좌번호
        String out_account = tmpAccount.getAccountId();
        //Dto setting
        CashOutReqDto dto = CashOutReqDto.builder().userIdx(celub.getUser().getIdx()).inAccId(req.getAccountId()).outAccId(out_account).memo(req.getMemo()).hashtag(req.getHashtag()).amount(req.getAmount()).transType(DEPOSIT).currency(Currency.KRW).build();
        accountService.cashOut(dto);

        return success(CELUBLOG_ACCOUNT_IN_SUCCESS);
    }
    //출금
    public SuccessResult celubOut(AccOutReqDto req){

        //Dto setting
        CashOutReqDto dto = CashOutReqDto.builder().userIdx(req.getUserIdx()).hashtag("출금").inAccId(req.getInAccId()).outAccId(req.getOutAccId()).memo(req.getMemo()).amount(req.getAmount()).transType(WITHDRAW).currency(Currency.KRW).build();
        accountService.cashOut(dto);


        return success(CELUBLOG_ACCOUNT_OUT_SUCCESS);
    }
    //사용자가 입력한 규칙 추가
    public SuccessResult celubAddRules(AddRuleReqDto dto){
        Celublog celublog = celublogRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new BaseException(CELEBRITY_NOT_FOUND_ACCOUNT));
        rulesRepository.deleteRules(dto.getAccountId());
        dto.getRuleList().forEach(rule->{
            Rules rules = Rules.builder().ruleName(rule.getRuleName()).ruleMoney(rule.getRuleMoney()).celublog(celublog).build();
            rulesRepository.save(rules);
        });
        Celublog ruleList = celublogRepository.findByAccountId(dto.getAccountId());
        return success(CELUBLOG_ADD_RULES_SUCCESS, ruleList.getRuleList());
    }

    //선택한 계좌 상세 내용 조회
    public SuccessResult celubAccDetail(String accountId){
        //accountInfo, ruleInfoList celub에 담겨옴
        Celublog celub = celublogRepository.findByAccountId(accountId);
        LocalDateTime today = LocalDateTime.now();
        long duration = ChronoUnit.DAYS.between(celub.getCreateDate(), today);
        AccDetailResDto.AccInfo accInfo = new AccDetailResDto.AccInfo(celub.getAccountId(), celub.getBalance(), celub.getName(), celub.getImgSrc(), celub.getOutAcc(), celub.getCelebrity(), duration, celub.getCreateDate(), celub.getOutAcc().getBalance());
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
            AccListResDto dto = new AccListResDto(tmp.getName(), tmp.getAccountId() , tmp.getBalance(), tmp.getImgSrc(), tmp.getCreateDate());
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
                .balance(0L)
                .build();

        celublogRepository.save(celub);
        return success(CELUBLOG_CREATED_SUCCESS, accNum);
    }

    private Deposit getDepositByAccId(String outAccId){
        return depositRepository.findById(outAccId).orElseThrow(()->new BaseException(DEPOSIT_NOT_FOUND));
    }
    private Celublog getCelubByAccId(String accId) {
        return celublogRepository.findByAccountId(accId);
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
