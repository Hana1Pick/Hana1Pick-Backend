package com.hana.hana1pick.domain.acchistory.service;

import com.hana.hana1pick.domain.acchistory.dto.request.AccHistoryReqDto;
import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryResDto;
import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.repository.AccHistoryRepository;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hana.hana1pick.domain.common.entity.AccountStatus.INACTIVE;
import static com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccHistoryService {

  private final AccHistoryRepository accHistoryRepository;
  private final UserRepository userRepository;
  private final AccountsRepository accountsRepository;

  // 계좌 내역 조회
  public SuccessResult<List<AccHistoryResDto>> getAccountHistory(AccHistoryReqDto request) {

    // 사용자, 계좌 정보 가져오기
    User user = getUserByIdx(request.getUserIdx());
    // 요청 계좌번호 저장
    String reqAccountId = request.getAccountId();

    Accounts account = getAccByUserIdxAndAccId(request.getUserIdx(), reqAccountId);

    // 예외처리
    exceptionHandling(user, account);

    // 거래 내역 조회
    List<AccountHistory> accountHistories = accHistoryRepository.findByAccCode(reqAccountId);

    // 로그 찍기
    log.info("[getAccountHistory]");

    // 반환 값 생성
    List<AccHistoryResDto> accHisList = accountHistories.stream()
            .map(ah -> makeAccHistoryResDto(ah, reqAccountId, user))
            .collect(Collectors.toList());

    // 응답 데이터 로그 출력
    log.info("[getAccountHistory] accHisList: {}", accHisList.stream().toString());

    return success(ACCOUNT_HISTORY_SUCCESS, accHisList);
  }

  // 입금인지 출금인지 확인하고 반환 객체 생성
  private AccHistoryResDto makeAccHistoryResDto(AccountHistory accountHistory, String reqAccountId, User user) {
    // 요청 계좌번호가 입금인지 출금인지 확인
    boolean isDeposit = accountHistory.getInAccId().equals(reqAccountId);

    // target: 입금 계좌일 경우 출금 계좌를, 출금 계좌일 경우 입금 계좌를 반환
    String target = isDeposit ? accountHistory.getOutAccId() : accountHistory.getInAccId();

    // target 계좌 정보를 가져옴
    Accounts targetAccount = getAccByAccId(target);

    // a계좌가 입금 계좌이고, a계좌가 celublog일 경우 target에 메모와 해시태그 담기
    if (isDeposit && targetAccount.getAccountType().equals("celublog")) {
      String memo = accountHistory.getMemo(); // 메모 정보
      String hashtags = accountHistory.getHashtag(); // 해시태그 정보
      target = String.format("%s, Memo: %s, Hashtags: %s", target, memo, hashtags);
    }else{
      // target 계좌의 사용자 이름 가져오기



    }

    // 이체 내역(입금/출금)
    Long transAmount = isDeposit ? accountHistory.getTransAmount() : -accountHistory.getTransAmount();

    // 잔액
    Long balance = isDeposit ? accountHistory.getAfterInBal() : accountHistory.getAfterOutBal();

    return AccHistoryResDto.builder()
            .transDate(accountHistory.getTransDate())
            .transType(accountHistory.getTransType())
            .target(target)
            .transAmount(transAmount)
            .balance(balance)
            .build();

    // target 계좌번호로 계좌 내역 가져오기
    List<AccountHistory> accountHistories = accHistoryRepository.findByAccCode(target);

    // getAccByAccId(target);

    //

//    return AccHistoryResDto.builder()
//            .transDate(accountHistory.getTransDate())
//            .transType(accountHistory.getTransType())
//            .target(target)
//            .transAmount(accountHistory.getTransAmount())
//            .balance(isDeposit ? accountHistory.getAfterInBal() : accountHistory.getAfterOutBal())
//            .build();
    return null;
  }
  private Accounts getAccByAccId(String accountId) {
    return (Accounts) accountsRepository.findByAccountId(accountId)
            .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
  }


  private User getUserByIdx(UUID userIdx) {
    return userRepository.findById(userIdx)
            .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
  }

  private Accounts getAccByUserIdxAndAccId(UUID userIdx, String accountId) {
    return accountsRepository.findByUserIdxAndAccountId(userIdx, accountId)
            .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
  }

  private void exceptionHandling(User user, Accounts account) {
    // 사용자의 계좌가 아닌 경우
    if (!account.getUserIdx().equals(user.getIdx())) {
      throw new BaseException(NOT_ACCOUNT_OWNER);
    }

    // 해지된 계좌인 경우
    if (account.getAccountStatus().equals(INACTIVE)) {
      throw new BaseException(ACCOUNT_INACTIVE);
    }
  }
}
