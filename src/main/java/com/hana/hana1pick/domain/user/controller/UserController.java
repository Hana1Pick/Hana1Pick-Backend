package com.hana.hana1pick.domain.user.controller;

import com.hana.hana1pick.domain.user.dto.request.UserCreateReqDto;
import com.hana.hana1pick.domain.user.dto.response.PwCheckResDto;
import com.hana.hana1pick.domain.user.dto.request.PwCheckReqDto;
import com.hana.hana1pick.domain.user.dto.response.UserCreateResDto;
import com.hana.hana1pick.domain.user.service.UserService;
import com.hana.hana1pick.global.exception.BaseResponse;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hana.hana1pick.global.exception.BaseResponseStatus.CELUBLOG_CREATED_SUCCESS;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "비밀번호 확인")
    @PostMapping("/password-check")
    public SuccessResult<PwCheckResDto> checkAccPw(@RequestBody PwCheckReqDto request) {
        return userService.checkPw(request);
    }

    @Operation(summary = "회원 정보 저장")
    @PostMapping("/signup")
    public SuccessResult<UserCreateResDto> signup(@RequestBody UserCreateReqDto userCreateReqDto) {
        log.info("회원가입 시도 : {}", userCreateReqDto);
        return BaseResponse.success(CELUBLOG_CREATED_SUCCESS, userService.save(userCreateReqDto));
    }
}
