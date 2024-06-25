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

import java.util.UUID;

 @RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "이체받을 계좌 목록 추출")
    @GetMapping("/cash-out")
    public BaseResponse.SuccessResult<AccountForCashOutResDto> getAccountForCashOut(@RequestParam UUID userIdx, @RequestParam String outAccId) {
        AccountForCashOutReqDto request = new AccountForCashOutReqDto(userIdx, outAccId);
        return accountService.getAccountForCashOut(request);
    }

    @Operation(summary = "이체받을 계좌 목록 검색")
    @GetMapping("/cash-out/history")
    public BaseResponse.SuccessResult<AccountForCashOutHisResDto> getAccountHistoryForCashOut(@RequestParam String outAccId, @RequestParam String query) {
        AccountForCashOutHisReqDto request = new AccountForCashOutHisReqDto(outAccId, query);
        return accountService.getAccountHistoryForCashOut(request);
    }
}
