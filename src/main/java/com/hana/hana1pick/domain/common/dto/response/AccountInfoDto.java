package com.hana.hana1pick.domain.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AccountInfoDto {
    private String accountType;
    private String accountId;
    private String name;
}
