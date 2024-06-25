package com.hana.hana1pick.domain.acchistory.controller;
import com.hana.hana1pick.domain.acchistory.dto.request.AccHistoryReqDto;

import com.hana.hana1pick.domain.acchistory.dto.response.AccHistoryResDto;
import com.hana.hana1pick.domain.acchistory.service.AccHistoryService;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}