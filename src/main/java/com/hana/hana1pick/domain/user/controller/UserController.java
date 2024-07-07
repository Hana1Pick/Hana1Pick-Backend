package com.hana.hana1pick.domain.user.controller;

import com.hana.hana1pick.domain.common.dto.response.AccountResDto;
import com.hana.hana1pick.domain.user.dto.request.PwCheckReqDto;
import com.hana.hana1pick.domain.user.dto.request.UserOCRReqDto;
import com.hana.hana1pick.domain.user.dto.request.UserUpdateReqDto;
import com.hana.hana1pick.domain.user.dto.response.PwCheckResDto;
import com.hana.hana1pick.domain.user.dto.response.UserInfoResDto;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.service.KakaoService;
import com.hana.hana1pick.domain.user.service.UserService;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import com.hana.hana1pick.global.util.FileUpload;
import com.hana.hana1pick.global.util.OCR;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.redis.core.RedisTemplate;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
    private final RedisTemplate<String, Object> redisTemplate;

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
            User newUser = userService.saveUserWithEmailAndProfile(userInfo.getEmail(), userInfo.getProfile());
            userInfo.setUserIdx(newUser.getIdx());
            userInfo.setNation(newUser.getNation().getValue());
            return success(JOIN_SUCCESS, userInfo);
        } else {
            userInfo.setUserIdx(user.getIdx());
            userInfo.setName(user.getName());
            userInfo.setNation(user.getNation().getValue());
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
    @PutMapping("/update")
    public BaseResponse.SuccessResult updateUserInfo(@RequestBody UserUpdateReqDto request) {
        return userService.updateUserInfo(request);
    }

    @Operation(summary = "사용자 계좌 목록 타입별 조회")
    @GetMapping("/account-list")
    public SuccessResult<List<AccountResDto>> getAccountsByType(@RequestParam("userIdx") UUID userIdx, @RequestParam("type") String type) {
        return userService.getAccountsByType(userIdx, type);
    }


    @PostMapping("/ocr")
    public Object ocrimpl(UserOCRReqDto ocrDto, @RequestParam("file") MultipartFile uploadImg, HttpSession session) {
        try {
            log.info("Uploaded image name: " + uploadImg.getOriginalFilename());
            String imgname = "uploads/" + uploadImg.getOriginalFilename();

                FileUpload.saveFile(uploadImg);

                JSONObject jsonObject = OCR.getResult(imgname);
                log.info("OCR result: " + jsonObject.toString()); // JSON 객체 로깅

                if (jsonObject == null || !jsonObject.containsKey("images")) {
                    log.error("OCR result does not contain 'images' key or is null");
                    return new BaseException(INVALID_REDIS_KEY);
                }
                Map<String, Object> map = OCR.getData(jsonObject);

                log.info("map 정보: " + map);
                return success(DEPOSIT_OCR_SUCCESS, map);
        } catch (IOException e) {
            log.error("File upload failed", e);
            return new BaseException(FAIL_TO_UPLOAD_FILE);
        }
    }

    @Operation(summary = "사용자 정보 조회")
    @PostMapping("/info")
    public SuccessResult<UserInfoResDto> getUser(@RequestParam("email") String email) {
        return userService.getUser(email);
    }
}