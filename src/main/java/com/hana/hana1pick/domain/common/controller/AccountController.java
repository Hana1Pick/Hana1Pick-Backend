 package com.hana.hana1pick.domain.common.controller;

import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutHisReqDto;
import com.hana.hana1pick.domain.common.dto.response.AccountForCashOutHisResDto;
import com.hana.hana1pick.domain.common.dto.response.AccountForCashOutResDto;
import com.hana.hana1pick.domain.common.service.AccountService;
import com.hana.hana1pick.domain.common.dto.request.AccountForCashOutReqDto;
import com.hana.hana1pick.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "이체받을 계좌 목록 추출")
    @PostMapping("/cash-out")
    public BaseResponse.SuccessResult<AccountForCashOutResDto> getAccountForCashOut(@RequestBody AccountForCashOutReqDto request) {
        return accountService.getAccountForCashOut(request);
    }

    @Operation(summary = "이체받을 계좌 목록 검색")
    @PostMapping("/cash-out/history")
    public BaseResponse.SuccessResult<AccountForCashOutHisResDto> getAccountHistoryForCashOut(@RequestBody AccountForCashOutHisReqDto request) {
        return accountService.getAccountHistoryForCashOut(request);
    }
}
