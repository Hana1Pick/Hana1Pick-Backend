package com.hana.hana1pick.domain.acchistory.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AccHistoryResDto {
    private LocalDateTime transDate;
    private String transType;
    private String target;
    private Long transAmount;
    private Long balance;
}
