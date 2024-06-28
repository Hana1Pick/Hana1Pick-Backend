package com.hana.hana1pick.domain.deposit.dto.request;


import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.entity.UserNation;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositCreateReqDto {
    private Long balance;
    private AccountStatus status;
    private String accountId;
    private String name;
    private String email;
}