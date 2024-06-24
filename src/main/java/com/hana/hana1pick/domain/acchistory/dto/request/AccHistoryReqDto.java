package com.hana.hana1pick.domain.acchistory.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Data
public class AccHistoryReqDto {
    private UUID userIdx;
    private String accountId;
}