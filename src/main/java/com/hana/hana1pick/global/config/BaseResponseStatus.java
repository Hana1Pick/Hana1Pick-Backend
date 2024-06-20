package com.hana.hana1pick.global.config;

import lombok.Getter;
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS1(200, "요청에 성공하였습니다."),
    SUCCESS2(201, "생성에 성공하였습니다."),

    /**
     * 202 : Request 오류
     */
    // Common
    SYSTEM_ERROR(202, "알 수 없는 오류 서버팀에 문의주세요."),
    REQUEST_ERROR( 203, "입력값을 확인해주세요.");



    private final int code;
    private final String message;

    private BaseResponseStatus(int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.code = code;
        this.message = message;
    }
}