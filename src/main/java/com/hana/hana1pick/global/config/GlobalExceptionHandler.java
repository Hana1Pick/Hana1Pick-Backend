package com.hana.hana1pick.global.config;

import com.hana.hana1pick.global.config.BaseResponse.ErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler
    public ErrorResult handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return BaseResponse.fail(BaseResponseStatus.SYSTEM_ERROR);
    }
    @ExceptionHandler({BaseException.class})
    public ErrorResult handleBaseException(BaseException e) {
        return BaseResponse.fail(e.getStatus());
    }

}
