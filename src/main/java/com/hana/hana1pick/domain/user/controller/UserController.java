package com.hana.hana1pick.domain.user.controller;

import com.hana.hana1pick.domain.common.dto.response.AccountResDto;
import com.hana.hana1pick.domain.user.dto.request.PwCheckReqDto;
import com.hana.hana1pick.domain.user.dto.request.UserUpdateReqDto;
import com.hana.hana1pick.domain.user.dto.response.PwCheckResDto;
import com.hana.hana1pick.domain.user.dto.response.UserInfoResDto;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.service.KakaoService;
import com.hana.hana1pick.domain.user.service.UserService;
import com.hana.hana1pick.global.exception.BaseResponse;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @Operation(summary = "비밀번호 확인")
    @PostMapping("/password-check")
    public SuccessResult<PwCheckResDto> checkAccPw(@RequestBody PwCheckReqDto request) {
        return userService.checkPw(request);
    }

    @Operation(summary = "카카오 로그인")
    @GetMapping("/login")
    public SuccessResult<UserInfoResDto> login(@RequestParam("accessToken") String accessToken) {
        // 1. AccessToken을 통해 userInfo 추출 하기
        // 2. DB 속 회원 정보 유무 확인 : 유 - 정보 뽑아서 리턴 / 무 - 회원 가입 후 리턴
        log.info(accessToken);

        UserInfoResDto userInfo = kakaoService.getUserInfo(accessToken); // 3. AccessToken을 통해 userInfo 추출 하기
        log.info("프사와 이메일을 가져왔어용");
        log.info("프사: " + userInfo.getProfile());
        log.info("이메일: " + userInfo.getEmail());

        // 4. DB에 회원 정보 저장하기
        // 만약 회원 정보가 이미 존재한다면, 로그인 처리만 하기
        // 만약 회원 정보가 존재하지 않는다면, 회원 정보 저장 후 로그인 처리하기
        User user = userService.findUserByEmail(userInfo.getEmail());

        if (user == null) {
            userService.saveUserWithEmailAndProfile(userInfo.getEmail(), userInfo.getProfile());
            return success(JOIN_SUCCESS, userInfo);
        } else {
            userInfo.setUserIdx(user.getIdx());
            userInfo.setName(user.getName());
            return success(LOGIN_SUCCESS, userInfo);
        }
    }

    @Operation(summary = "사용자의 전체 계좌 목록 조회")
    @GetMapping("/accounts/list")
    public SuccessResult<List<AccountResDto>> getAllAccountsByUserId(@RequestParam("userIdx") UUID userIdx) {
        List<AccountResDto> accounts = userService.getAllAccountsByUserId(userIdx);
        // 메시지 변경
        return success(ACCOUNT_LIST_SUCCESS, accounts);
    }

    @Operation(summary = "사용자 정보 수정")
    @PostMapping("/update")
    public BaseResponse.SuccessResult updateUserInfo(@RequestBody UserUpdateReqDto request) {
        return userService.updateUserInfo(request);
    }

    @Operation(summary = "사용자 계좌 목록 타입별 조회")
    @GetMapping("/account-list")
    public SuccessResult<List<AccountResDto>> getAccountsByType(@RequestParam("userIdx") UUID userIdx, @RequestParam("type") String type) {
        return userService.getAccountsByType(userIdx, type);
    }
}