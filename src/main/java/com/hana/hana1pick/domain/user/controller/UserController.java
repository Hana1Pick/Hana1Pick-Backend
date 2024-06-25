package com.hana.hana1pick.domain.user.controller;

import com.hana.hana1pick.domain.user.dto.response.PwCheckResDto;
import com.hana.hana1pick.domain.user.dto.request.PwCheckReqDto;
import com.hana.hana1pick.domain.user.service.UserService;
import com.hana.hana1pick.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "비밀번호 확인")
    @PostMapping("/password-check")
    public BaseResponse.SuccessResult<PwCheckResDto> checkAccPw(@RequestBody PwCheckReqDto request) {
        return userService.checkPw(request);
    }
}
