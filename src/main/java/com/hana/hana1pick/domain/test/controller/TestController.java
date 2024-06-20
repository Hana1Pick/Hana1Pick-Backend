package com.hana.hana1pick.domain.test.controller;

import com.hana.hana1pick.global.config.BaseException;
import com.hana.hana1pick.global.config.BaseResponse;
import com.hana.hana1pick.global.config.BaseResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;
import com.hana.hana1pick.global.config.BaseResponse.ErrorResult;


import static com.hana.hana1pick.global.config.BaseResponseStatus.SUCCESS1;
import static com.hana.hana1pick.global.config.BaseResponseStatus.SUCCESS2;

@RestController
@RequestMapping("/test/api")
public class TestController {



    @Operation(summary = "테스트1")
    @GetMapping("/hello1")
    public BaseResponse.SuccessResult hello1(@RequestParam(required = false, defaultValue = "false") boolean throwError) {
        if (throwError) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        return BaseResponse.success(SUCCESS1);
    }

    @Operation(summary = "테스트2")
    @GetMapping("/hello2")
    public BaseResponse.SuccessResult<String> hello2(@RequestParam(required = false, defaultValue = "false") boolean throwError) {
        if (throwError) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        return BaseResponse.success(SUCCESS2, "hello");
    }
}