package com.hana.hana1pick.domain.celublog.dto.response;

import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import com.hana.hana1pick.domain.celublog.entity.Rules;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AccDetailResDto {
    private AccInfo accountInfo;
    private List<Rules> ruleInfo;
    private List<AccReport> accountReport;
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Getter
    public static class AccInfo {
        private String accountId;
        private long balance;
        private String name;
        private String imgSrc;
        private String outAccId;
        private long celebrityIdx;
        private long duration;
        private LocalDate createDate;
        private long outAccBalance;

        public AccInfo(String accountId,Long balance, String name, String imgSrc, Deposit outAcc, Celebrity celebrity, Long duration, LocalDate createDate, Long outAccBalance) {
            this.accountId = accountId;
            this.balance = balance;
            this.name = name;
            this.imgSrc = imgSrc;
            this.outAccId = outAcc.getAccountId();
            this.celebrityIdx = celebrity.getIdx();
            this.duration = duration;
            this.createDate = createDate;
            this.outAccBalance = outAccBalance;
        }
    }
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Getter
    public static class AccReport{
        private String transDate;
        private String memo;
        private long transAmount;
        private long afterInBal;
        private String hashtag;
        public AccReport(String transDate, String memo, Long transAmount, Long afterInBal, String hashtag){
            this.transDate = transDate;
            this.memo = memo;
            this.transAmount = transAmount;
            this.afterInBal = afterInBal;
            this.hashtag = hashtag;
        }
    }
}
