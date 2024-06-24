package com.hana.hana1pick.domain.acchistory.dto.response;

import com.hana.hana1pick.domain.acchistory.entity.TransType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AccHistoryResDto {

    private LocalDateTime transDate;
    private TransType transType;
    private String target;
    private Long transAmount;
    private Long balance;
}
