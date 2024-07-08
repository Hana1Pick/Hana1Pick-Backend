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
    LOGIN_SUCCESS(OK, "카카오 로그인 성공"),
    JOIN_SUCCESS(CREATED, "카카오 회원가입 성공"),
    ACCOUNT_LIST_SUCCESS(OK, "계좌 목록 조회 성공"),
    USER_UPDATE_SUCCESS(OK, "사용자 정보 수정 성공"),
    USER_INFO_SUCCESS(OK, "사용자 정보 조회 성공"),

    //Deposit
    DEPOSIT_CREATED_SUCCESS(CREATED, "입출금 계좌 개설 성공"),
    DEPOSIT_OCR_SUCCESS(OK, "외국인등록증 OCR 성공"),
    DEPOSIT_FETCH_SUCCESS(OK, "입출금 계좌 조회 성공"),

    // MoaClub
    MOACLUB_CREATED_SUCCESS(CREATED, "모아클럽 개설 성공"),
    MOACLUB_INVITE_SUCCESS(OK, "모아클럽 초대 성공"),
    MOACLUB_JOIN_SUCCESS(OK, "모아클럽 가입 성공"),
    MOACLUB_UPDATE_SUCCESS(OK, "모아클럽 수정 성공"),
    MOACLUB_MEMBER_PW_UPDATE_SUCCESS(OK, "모아클럽 멤버 비밀번호 수정 성공"),
    MOACLUB_FETCH_SUCCESS(OK, "모아클럽 조회 성공"),
    MOACLUB_FEE_STATUS_FETCH_SUCCESS(OK, "모아클럽 회비 내역 조회 성공"),
    MOACLUB_MEMBER_LEAVE_SUCCESS(OK, "모아클럽 탈퇴 성공"),
    MOACLUB_REQUEST_SUCCESS(OK, "모아클럽 요청 성공"),
    MOACLUB_VOTE_SUCCESS(OK, "모아클럽 투표 성공"),
    MOACLUB_REQUEST_FETCH_SUCCESS(OK, "모아클럽 요청 조회 성공"),
    MOACLUB_AUTO_TRANSFER_SET_SUCCESS(CREATED, "모아클럽 자동이체 설정 성공"),
    MOACLUB_MANAGER_CHECK_SUCCESS(OK, "모아클럽 관리자 확인 성공"),
    MOACLUB_MEMBER_FETCH_SUCCESS(OK, "모아클럽 멤버 리스트 조회 성공"),
    MOACLUB_AUTO_TRANSFER_FETCH_SUCCESS(OK, "모아클럽 자동이체 조회 성공"),
    
    // Chat
    CHAT_MESSAGE_LIST_LOAD_SUCCESS(OK, "채팅방 내 메시지 내역 조회 성공"),
    CHAT_MESSAGE_CREATED_SUCCESS(CREATED, "채팅 메시지 저장 성공"),

    // Celublog
    CELUBLOG_CREATED_SUCCESS(CREATED, "셀럽로그 개설 성공"),
    CELUBLOG_ACCOUNT_LIST_SUCCESS(OK, "셀럽로그 계좌 목록 조회 성공"),
    CELUBLOG_ACCOUNT_DETAIL_SUCCESS(OK, "셀럽로그 계좌 상세 조회 성공"),
    CELUBLOG_ADD_RULES_SUCCESS(CREATED, "셀럽로그 규칙 추가 성공"),
    CELUBLOG_ACCOUNT_IN_SUCCESS(OK, "셀럽로그 입금 성공"),
    CELUBLOG_ACCOUNT_OUT_SUCCESS(OK, "셀럽로그 출금 성공"),
    CELUBLOG_CELUBLIST_SUCCESS(OK, "셀럽로그 연예인 조회 성공"),
    CELUBLOG_SEARCH_CELUBLIST_SUCCESS(OK, "셀럽로그 연예인 검색 성공"),
    CELUBLOG_MODIFY_CELUBLIST_SUCCESS(OK, "셀럽로그 계좌 정보 변경 성공"),
  
    // Account
    ACCOUNT_CASH_OUT_LIST_SUCCESS(OK, "계좌 번호 목록 조회 성공"),
    ACCOUNT_CASH_OUT_HISTORY_LIST_SUCCESS(OK, "계좌 번호 목록 검색 성공"),
    ACCOUNT_CASH_OUT_SUCCESS(OK, "계좌이체 성공"),
    AUTO_TRANSFER_DELETE_SUCCESS(OK, "자동이체 삭제 성공"),

    // Account History
    ACCOUNT_HISTORY_SUCCESS(OK , "계좌 내역 조회 성공"),
    ACCOUNT_HISTORY_FOR_QR_SUCCESS(OK, "QR 속 계좌번호에 대한 거래내역 조회 성공"),

    // Notification
    NOTIFICATION_FETCH_SUCCESS(OK, "알림 목록 조회 성공"),
    NOTIFICATION_CHECK_SUCCESS(OK, "알림 확인 성공"),
    NOTIFICATION_DELETE_SUCCESS(OK, "알림 삭제 성공"),

    // Exchange
    EXCHANGE_INFO_FETCH_SUCCESS(OK, "환전 정보 조회 성공"),


    /**
     * 202 : Request 오류
     */
    // Common
    SYSTEM_ERROR(ACCEPTED, "E100","알 수 없는 오류 서버팀에 문의주세요."),
    REQUEST_ERROR(ACCEPTED, "E101","입력값을 확인해주세요."),

    // Deposit
    DEPOSIT_NOT_FOUND(ACCEPTED, "E300", "존재하지 않는 계좌입니다."),
    NOT_ACCOUNT_OWNER(ACCEPTED, "E301", "계좌의 소유자가 아닙니다."),
    INVALID_TRANSFER_AMOUNT(ACCEPTED, "E302", "유효하지 않은 금액입니다."),

    INVALID_REDIS_KEY(ACCEPTED, "E303", "유효하지 않는 key 입니다."),

    IMAGE_NOT_FOUND(ACCEPTED, "E304", "유효하지 않은 image 입니다."),

    FAIL_TO_UPLOAD_FILE(ACCEPTED, "E305", "파일 업로드에 실패했습니다."),

    // User
    USER_NOT_FOUND(ACCEPTED, "E200", "존재하지 않는 회원입니다."),
    USER_TRSF_LIMIT_NOT_FOUND(ACCEPTED, "E201", "회원 이체한도를 조회할 수 없습니다"),
    USER_TRSF_LIMIT_OVER(ACCEPTED,"E202", "회원 이체한도를 초과했습니다."),

    // Account
    ACCOUNT_NOT_FOUND(ACCEPTED, "E300", "존재하지 않는 계좌입니다."),
    ACCOUNT_INACTIVE(ACCEPTED, "E303", "해지된 계좌입니다."),

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
    MOACLUB_REQUEST_NOT_FOUND(ACCEPTED, "E409", "존재하지 않는 요청입니다."),
    NO_PERMISSION_TO_VOTE(ACCEPTED, "E410", "투표 권한이 없습니다."),
  
    // Celublog
    CELEBRITY_NOT_FOUND(ACCEPTED, "E500", "존재하지 않는 연예인입니다."),
    CELEBRITY_NOT_FOUND_ACCOUNT(ACCEPTED, "E501", "유효하지 않은 계좌입니다."),
    CELEBRITY_UPLOAD_FAIL(ACCEPTED, "E502", "사진 업로드 실패"),

    // Account
    ACCOUNT_STATUS_INVALID(ACCEPTED, "E600", "유효하지 않는 계좌입니다."),
    ACCOUNT_CASH_OUT_FAIL(ACCEPTED, "E601", "계좌이체를 실패했습니다."),

    // AutoTransfer
    AUTO_TRANSFER_NOT_FOUND(ACCEPTED, "E700", "자동이체를 찾을 수 없습니다"),

    // Notification
    NOTIFICATION_NOT_FOUND(ACCEPTED, "E800", "알림을 찾을 수 없습니다.")
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