package com.hana.hana1pick.global.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@Getter
public class BaseResponse {//BaseResponse 객체를 사용할때 성공, 실패 경우

    public static <T> SuccessResult<T> success(BaseResponseStatus status, T result) {
        return new SuccessResult<>(status.getCode(), status.getMessage(), result);
    }

    public static <T> SuccessResult<T> success(BaseResponseStatus status) {
        return new SuccessResult<>(status.getCode(), status.getMessage());
    }

    public static ErrorResult fail(BaseResponseStatus status) {
        return new ErrorResult(status);
    }

    @Getter
    @AllArgsConstructor
    public static class SuccessResult<T> {
        private final int status;
        private final String message;
        @JsonInclude(NON_NULL)
        private T data;

        private SuccessResult(int status, String message) {
            this.message = message;
            this.status = status;
        }
    }

    @Getter
    public static class ErrorResult {
        private final int status;
        private final String message;
        private ErrorResult(BaseResponseStatus baseResponseStatus) {
            this.message = baseResponseStatus.getMessage();
            this.status = baseResponseStatus.getCode();
        }
    }

}

