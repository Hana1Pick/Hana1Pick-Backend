package com.hana.hana1pick.domain.common.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AccountForCashOutResDto {
    private List<AccountInfoDto> myAccId;
    private List<AccountInfoDto> recentAccId;
}
