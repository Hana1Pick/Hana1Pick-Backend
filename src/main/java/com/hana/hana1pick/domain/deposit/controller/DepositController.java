package com.hana.hana1pick.domain.deposit.controller;


import com.hana.hana1pick.domain.deposit.dto.request.DepositAccIdReqDto;
import com.hana.hana1pick.domain.deposit.dto.request.DepositCreateReqDto;
import com.hana.hana1pick.domain.deposit.dto.response.DepositCreateResDto;
import com.hana.hana1pick.domain.deposit.dto.response.DepositDetailResDto;
import com.hana.hana1pick.domain.deposit.service.DepositService;
import com.hana.hana1pick.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deposit")
@Slf4j
public class DepositController {

    private final DepositService depositService;

    @Operation(summary = "입출금 계좌 개설")
    @PostMapping
    public BaseResponse.SuccessResult<DepositCreateResDto> openDeposit(@RequestBody DepositCreateReqDto request) {
        return depositService.createDeposit(request);
    }

    @Operation(summary = "입출금 계좌 조회")
    @PostMapping("/detail")
    public BaseResponse.SuccessResult<DepositDetailResDto> getDeposit(@RequestBody DepositAccIdReqDto request) {
        return depositService.getDepositDetail(request);
    }
}
