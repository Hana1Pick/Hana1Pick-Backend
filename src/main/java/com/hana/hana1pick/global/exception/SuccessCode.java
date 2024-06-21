package com.hana.hana1pick.global.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    //common
    USER_REGISTER_CHECK_SUCCESS(OK, "회원가입 여부 확인 성공");

    private final HttpStatus httpStatus;
    private final String message;


    public int getHttpStatusValue() {
        return httpStatus.value();
    }
}