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
    // User
    USER_PW_CHECK_SUCCESS(OK, "사용자 비밀번호 확인 성공"),

    // MoaClub
    MOACLUB_CREATED_SUCCESS(CREATED, "모아클럽 개설 성공"),
    MOACLUB_INVITE_SUCCESS(OK, "모아클럽 초대 성공"),
    MOACLUB_JOIN_SUCCESS(OK, "모아클럽 가입 성공"),
    MOACLUB_UPDATE_SUCCESS(OK, "모아클럽 수정 성공"),
    MOACLUB_MEMBER_PW_UPDATE_SUCCESS(OK, "모아클럽 멤버 비밀번호 수정 성공"),
    MOACLUB_FETCH_SUCCESS(OK, "모아클럽 조회 성공"),
    MOACLUB_FEE_STATUS_FETCH_SUCCESS(OK, "모아클럽 회비 내역 조회 성공"),
    MOACLUB_MEMBER_LEAVE_SUCCESS(OK, "모아클럽 탈퇴 성공"),
    MOACLUB_MANAGER_REQUEST_SUCCESS(OK, "모아클럽 요청 성공"),
    MOACLUB_REQUEST_FETCH_SUCCESS(OK, "모아클럽 요청 조회 성공"),

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
    MOACLUB_NOT_FOUND(ACCEPTED, "E400", "존재하지 않는 모아클럽입니다."),
    NO_PERMISSION_TO_ACCESS_MOACLUB(ACCEPTED, "E401", "클럽 접근권한이 없습니다."),
    USER_ALREADY_JOINED(ACCEPTED, "E402", "이미 가입된 클럽입니다."),
    INACTIVE_MOACLUB(ACCEPTED, "E403", "해지된 클럽입니다."),
    NO_PERMISSION_TO_MANAGE(ACCEPTED, "E404", "클럽 권한이 없습니다."),
    USER_NOT_CLUB_MEMBER(ACCEPTED, "E405", "클럽 멤버가 아닙니다."),
    MOACLUB_MEMBER_NOT_FOUND(ACCEPTED, "E406", "존재하지 않는 클럽 멤버입니다."),
    MOACLUB_HAS_MEMBER(ACCEPTED, "E407", "클럽에 아직 멤버가 존재합니다"),
    REQUEST_ALREADY_PENDING(ACCEPTED, "E408", "이미 대기 중인 요청이 존재합니다"),
    MOACLUB_REQUEST_NOT_FOUND(ACCEPTED, "E409", "존재하지 않는 요청입니다.")
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