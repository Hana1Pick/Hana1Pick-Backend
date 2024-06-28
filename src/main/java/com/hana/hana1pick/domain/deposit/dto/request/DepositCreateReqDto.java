package com.hana.hana1pick.domain.deposit.dto.request;


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
    private UUID userIdx;
    private String name;

    public Deposit toEntity(){
        return Deposit.builder()
                .user(User.builder().idx(userIdx).build())
                .build();
    }
}