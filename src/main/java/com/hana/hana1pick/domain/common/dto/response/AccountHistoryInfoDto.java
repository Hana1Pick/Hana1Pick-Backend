package com.hana.hana1pick.domain.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountHistoryInfoDto {
    private String accountId;
    private String name;
    private Long balance;
}
