package com.hana.hana1pick.domain.common.entity;

public enum AccountStatus {
    ACTIVE(0),
    INACTIVE(1);

    private final int code;

    AccountStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static AccountStatus fromCode(int code) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AccountStatus code: " + code);
    }
}