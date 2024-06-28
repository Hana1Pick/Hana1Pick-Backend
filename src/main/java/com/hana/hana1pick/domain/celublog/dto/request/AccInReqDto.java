package com.hana.hana1pick.domain.celublog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
public class AccInReqDto {
    private String accountId; //셀럽로그 계좌
    private long amount;
    private String memo; //규칙
    private String hashtag; //해시태그
}
