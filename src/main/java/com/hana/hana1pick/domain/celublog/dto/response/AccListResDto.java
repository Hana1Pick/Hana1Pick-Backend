package com.hana.hana1pick.domain.celublog.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AccListResDto {
    private String name;
    private String account_id;
    private long balance;
    private String imgSrc;
    private LocalDate createDate;
}
