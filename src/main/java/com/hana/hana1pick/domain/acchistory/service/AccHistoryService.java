package com.hana.hana1pick.domain.acchistory.service;

import com.hana.hana1pick.domain.acchistory.dto.request.AccHistoryReqDto;
import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryResDto;
import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.repository.AccHistoryRepository;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
  private Accounts account;

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

    // 반환 값
    // List<AccHistoryResDto> accHisList = accountHistories.stream().map();

    // 반환 값 생성
    List<AccHistoryResDto> accHisList = accountHistories.stream()
            .map(ah -> makeAccHistoryResDto(ah, reqAccountId))
            .collect(Collectors.toList());

    // 응답 데이터 로그 출력
    log.info("[getAccountHistory] accHisList: {}", accHisList.stream().toString());

    return success(ACCOUNT_HISTORY_SUCCESS, accHisList);

  }

  // 입금인지 출금인지 확인하고 반환 객체 생성
  private AccHistoryResDto makeAccHistoryResDto(AccountHistory accountHistory, String reqAccountId) {
    boolean isDeposit = accountHistory.getInAccId().equals(reqAccountId);
    String target = isDeposit ? accountHistory.getOutAccId() : accountHistory.getInAccId();

    return AccHistoryResDto.builder()
            .transDate(accountHistory.getTransDate())
            .transType(accountHistory.getTransType())
            .target(target)
            .transAmount(accountHistory.getTransAmount())
            .balance(isDeposit ? accountHistory.getAfterInBal() : accountHistory.getAfterOutBal())
            .build();
  }

  private User getUserByIdx(UUID userIdx) {
    return userRepository.findById(userIdx)
            .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
  }

  private Accounts getAccByUserIdxAndAccId(UUID userIdx, String accountId) {
    return accountsRepository.findByIdAndAccountId(userIdx, accountId)
            .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
  }

  private void exceptionHandling(User user, Accounts account) {
    // 사용자의 계좌가 아닌 경우
    if (!account.getUserIdx().equals(user.getIdx())) {
      throw new BaseException(NOT_ACCOUNT_OWNER);
    }

    // 해지된 계좌인 경우
    if (account.getStatus() == INACTIVE) {
      throw new BaseException(ACCOUNT_INACTIVE);
    }
  }

}
