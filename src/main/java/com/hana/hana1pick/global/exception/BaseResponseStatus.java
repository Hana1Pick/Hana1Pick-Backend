package com.hana.hana1pick.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     * 201 : 생성 성공
     */
    // moaclub
    MOACLUB_CREATED_SUCCESS(CREATED, "모아클럽 개설 성공"),
    MOACLUB_INVITE_SUCCESS(OK, "모아클럽 초대 성공"),

    /**
     * 202 : Request 오류
     */
    // Common
    SYSTEM_ERROR(ACCEPTED, "E100","알 수 없는 오류 서버팀에 문의주세요."),
    REQUEST_ERROR(ACCEPTED, "E101","입력값을 확인해주세요."),

    // User
    USER_NOT_FOUND(ACCEPTED, "E200", "존재하지 않는 회원입니다."),

    // Deposit
    DEPOSIT_NOT_FOUND(ACCEPTED, "E300", "존재하지 않는 계좌입니다."),
    NOT_ACCOUNT_OWNER(ACCEPTED, "E301", "계좌의 소유자가 아닙니다."),
    INVALID_TRANSFER_AMOUNT(ACCEPTED, "E302", "유효하지 않은 금액입니다."),

    // MoaClub
    MOACLUB_NOT_FOUND(ACCEPTED, "E400", "존재하지 않는 모아클럽입니다.")
    ;

    private final HttpStatus httpStatus;
    private String errorCode;
    private final String message;

    public int getHttpStatusValue() {
        return httpStatus.value();
    }

    private BaseResponseStatus(HttpStatus httpStatus, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.httpStatus = httpStatus;
        this.message = message;
    }
}