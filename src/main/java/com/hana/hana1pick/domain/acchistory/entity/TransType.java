package com.hana.hana1pick.domain.acchistory.entity;

import lombok.Getter;

@Getter
public enum TransType {
    DEPOSIT("입금"),
    WITHDRAW("출금"),
    AUTO_TRANSFER("자동 이체");

    private final String value;

    TransType(String value) {
        this.value = value;
    }
}
