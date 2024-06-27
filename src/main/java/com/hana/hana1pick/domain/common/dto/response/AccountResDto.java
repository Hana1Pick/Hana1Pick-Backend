package com.hana.hana1pick.domain.common.dto.response;

import com.hana.hana1pick.domain.common.entity.Accounts;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class AccountResDto {
    private String accountId; // 계좌 번호
    private String name; // 계좌 이름
    private String accountType; // 계좌 타입
    private Long balance; // 잔액

    public static AccountResDto from(Accounts account) {
        return AccountResDto.builder()
                .accountId(account.getAccountId())
                .name(account.getName())
                .accountType(account.getAccountType())
                .balance(account.getAccountBalance())
                .build();
    }
}
