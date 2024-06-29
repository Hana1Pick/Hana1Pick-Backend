package com.hana.hana1pick.domain.acchistory.controller;
import com.hana.hana1pick.domain.acchistory.dto.request.AccHistoryReqDto;

import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryForQrResDto;
import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryResDto;
import com.hana.hana1pick.domain.acchistory.service.AccHistoryService;
import com.hana.hana1pick.global.exception.BaseResponse;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccHistoryController {

  private final AccHistoryService accHistoryService;

  @Operation(summary = "거래내역 조회")
  @PostMapping
  public SuccessResult<List<AccHistoryResDto>> getAccHis(@RequestBody AccHistoryReqDto request) {
    return accHistoryService.getAccountHistory(request);
  }

  @Operation(summary = "QR 속 이체받을 계좌의 최근 3개월간 거래내역 조회")
  @GetMapping("/qr/history")
  public BaseResponse.SuccessResult<AccHistoryForQrResDto> getAccHisForQr(@RequestParam String accId) {
    return accHistoryService.getAccountHistoryForQr(accId);
  }
}