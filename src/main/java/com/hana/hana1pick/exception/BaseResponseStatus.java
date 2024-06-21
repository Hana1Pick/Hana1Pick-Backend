package com.hana.hana1pick.exception;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(200, "요청에 성공했습니다.");

    private final int code;
    private final String message;
    private BaseResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
