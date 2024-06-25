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
    // 1. requestDto에서 값 꺼내기 : accountId
    String accountId = request.getAccountId();

    // 2. 예외처리
    validateAccount(accountId);
    log.info("Request accountId: {}", accountId);

    // 3. 거래 내역 조회 및 반환 값 생성
    List<AccHistoryResDto> accountHistories = findByAccountId(accountId);

    // 응답 데이터 로그 출력
    log.info("[getAccountHistory] accHisList: {}", accountHistories);

    return success(ACCOUNT_HISTORY_SUCCESS, accountHistories);
  }

  // 입금인지 출금인지 확인하고 반환 객체 생성
  private AccHistoryResDto makeAccHistoryResDto(AccountHistory accountHistory, String reqAccountId) {
    // 요청 계좌번호가 입금인지 출금인지 확인
    boolean isDeposit = accountHistory.getInAccId().equals(reqAccountId);

    // target: 입금 계좌일 경우 출금 계좌를, 출금 계좌일 경우 입금 계좌를 반환
    String target = reqAccountId;

    // target 계좌 정보를 가져옴
    Accounts targetAccount = getAccByAccId(target);

    // a계좌가 입금 계좌이고, a계좌가 celublog일 경우 target에 메모와 해시태그 담기
    if (isDeposit && targetAccount.getAccountType().equals("celublog")) {
      String memo = accountHistory.getMemo(); // 메모 정보
      String hashtags = accountHistory.getHashtag(); // 해시태그 정보
      target = String.format("규칙: %s, 해시태그: %s", memo, hashtags);
    } else {
      // target 계좌의 사용자 이름 가져오기
      User targetUser = getUserByIdx(targetAccount.getUserIdx());
      target = targetUser.getName();
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
  }

  private User getUserByIdx(UUID userIdx) {
    return userRepository.findById(userIdx)
            .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
  }

  private Accounts getAccByAccId(String accountId) {
    return accountsRepository.findByAccountId(accountId)
            .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
  }

  // response 객체 생성
  private List<AccHistoryResDto> findByAccountId(String accountId) {
    // 1. 계좌 내역 조회
    List<AccountHistory> result = accHistoryRepository.findByAccCode(accountId);
    // 2. 계좌 내역이 없는 경우
    if (result.isEmpty()) {
      throw new BaseException(ACCOUNT_NOT_FOUND);
    }
    // 3. 계좌 내역이 있는 경우
    return result.stream()
            .map(accountHistory -> makeAccHistoryResDto(accountHistory, accountId))
            .collect(Collectors.toList());
  }

  private void validateAccount(String accountId) {
    // 계좌가 존재하는지 확인
    Accounts account = accountsRepository.findByAccountId(accountId)
            .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));

    // 해지된 계좌인지 확인
    if (account.getAccountStatus().equals(INACTIVE)) {
      throw new BaseException(ACCOUNT_INACTIVE);
    }
  }
}
