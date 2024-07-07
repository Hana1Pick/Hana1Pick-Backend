package com.hana.hana1pick.domain.acchistory.service;

import com.hana.hana1pick.domain.acchistory.dto.request.AccHistoryReqDto;
import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryForQrResDto;
import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryResDto;
import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.entity.TransType;
import com.hana.hana1pick.domain.acchistory.repository.AccHistoryRepository;
import com.hana.hana1pick.domain.common.dto.response.AccountHistoryInfoDto;
import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.hana.hana1pick.domain.moaclub.entity.Currency.KRW;
import static com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccHistoryService {

  private final AccHistoryRepository accHistoryRepository;
  private final AccountsRepository accountsRepository;
  private final MoaClubRepository moaClubRepository;

  public SuccessResult<AccHistoryForQrResDto> getAccountHistoryForQr(String accountId) {
    // 1. 예외 처리
    validateAccount(accountId);

    // 2. 요청이 들어온 시각을 기준으로 3개월 전의 날짜 계산
    LocalDateTime startDate = LocalDateTime.now().minusMonths(3);
    
    // 3. DB에서 데이터 추출
    List<AccountHistory> result = accHistoryRepository.findRecentHistoryForAccount(startDate, accountId);

    return success(ACCOUNT_HISTORY_FOR_QR_SUCCESS, new AccHistoryForQrResDto(result.size()));
  }

  // 계좌 내역 조회
  public SuccessResult<List<AccHistoryResDto>> getAccountHistory(AccHistoryReqDto request) {
    // 1. requestDto에서 값 꺼내기 : accountId
    String accountId = request.getAccountId();

    // 2. 예외처리
    validateAccount(accountId);

    // 외화 거래인지 확인
    boolean isFx = false;
    if (accountId.substring(3, 5).equals("02")) {
        MoaClub moaClub = moaClubRepository.findById(accountId)
                .orElseThrow(() -> new BaseException(MOACLUB_NOT_FOUND));

        if (moaClub.getCurrency() != KRW) {
            isFx = true;
        }
    }

    // 3. 거래 내역 조회 및 반환 값 생성
    List<AccHistoryResDto> accountHistories = findByAccountId(accountId, isFx);


    return success(ACCOUNT_HISTORY_SUCCESS, accountHistories);
  }

  // 입금인지 출금인지 확인하고 반환 객체 생성
  private AccHistoryResDto makeAccHistoryResDto(AccountHistory accountHistory, String reqAccountId) {
    // 요청 계좌번호가 입금인지 출금인지 확인
    boolean isDeposit = accountHistory.getInAccId().equals(reqAccountId);

    // 조회하려는 계좌
    Accounts reqAccount = getAccByAccId(reqAccountId);

    // target: 입금 계좌일 경우 출금 계좌를, 출금 계좌일 경우 입금 계좌를 반환
    String target = isDeposit ? accountHistory.getOutAccId() : accountHistory.getInAccId();

    // target 계좌 정보를 가져옴
    Accounts targetAccount = getAccByAccId(target);

    // a계좌가 입금 계좌이고, a계좌가 celublog일 경우 target에 메모와 해시태그 담기
    if (isDeposit && reqAccount.getAccountType().equals("celublog")) {
      String memo = accountHistory.getMemo(); // 메모 정보
      String hashtags = accountHistory.getHashtag(); // 해시태그 정보
      target = String.format("규칙: %s, 해시태그: %s", memo, hashtags);
    } else {
      // target 계좌의 사용자 이름 가져오기
      target = targetAccount.getName();
    }

    // 이체 내역(입금/출금)
    Long transAmount = isDeposit ? accountHistory.getTransAmount() : -accountHistory.getTransAmount();

    // 거래 유형(입금/출금/자동이체)
    String transType;

    if ("AUTO_TRANSFER".equals(accountHistory.getTransType())) {
      transType = "AUTO_TRANSFER";
    } else {
      transType = transAmount < 0 ? "WITHDRAW" : "DEPOSIT";
    }

    // 잔액
    Long balance = isDeposit ? accountHistory.getAfterInBal() : accountHistory.getAfterOutBal();

    return AccHistoryResDto.builder()
            .transDate(accountHistory.getTransDate())
            .transType(transType)
            .target(target)
            .transAmount(transAmount)
            .balance(balance)
            .build();
  }

  private Accounts getAccByAccId(String accountId) {
    return accountsRepository.findById(accountId)
            .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
  }

  // response 객체 생성
  private List<AccHistoryResDto> findByAccountId(String accountId, boolean isFx) {
    // 1. 계좌 내역 조회
//    List<AccountHistory> result = accHistoryRepository.findByAccCode(accountId);
      List<AccountHistory> result = accHistoryRepository.findByAccCodeAndIsFx(accountId, isFx);

    // 2. 계좌 내역이 없는 경우 -> 예외처리 삭제, 빈 리스트 반환

    // 3. 계좌 내역이 있는 경우
    return result.stream()
            .map(accountHistory -> makeAccHistoryResDto(accountHistory, accountId))
            .collect(Collectors.toList());
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

  public void createAccountHistory(AccountHistoryInfoDto outAcc, AccountHistoryInfoDto inAcc, String memo, Long amount, TransType transType, String hashtag, boolean isFx) {
    AccountHistory accountHistory = AccountHistory.builder()
            .memo(memo)
            .transDate(LocalDateTime.now())
            .transType(transType)
            .transAmount(amount)
            .inAccId(inAcc.getAccountId())
            .inAccName(inAcc.getName())
            .outAccId(outAcc.getAccountId())
            .beforeInBal(inAcc.getBalance()-amount)
            .afterInBal(inAcc.getBalance())
            .beforeOutBal(outAcc.getBalance()+amount)
            .afterOutBal(outAcc.getBalance())
            .hashtag(hashtag)
            .isFx(isFx)
            .build();

    accHistoryRepository.save(accountHistory);
  }
}
