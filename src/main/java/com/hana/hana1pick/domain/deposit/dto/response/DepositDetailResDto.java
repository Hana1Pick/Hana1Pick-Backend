package com.hana.hana1pick.domain.deposit.dto.response;

import com.hana.hana1pick.domain.deposit.entity.Deposit;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class DepositDetailResDto {

    private String name;
    private String accountId;
    private Long balance;
    private LocalDate createDate;

    public static DepositDetailResDto of(Deposit deposit) {
        return DepositDetailResDto.builder()
                .name(deposit.getName())
                .accountId(deposit.getAccountId())
                .balance(deposit.getBalance())
                .createDate(deposit.getCreateDate())
                .build();
    }
}