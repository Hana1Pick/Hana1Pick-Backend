package com.hana.hana1pick.domain.celublog.controller;

import com.hana.hana1pick.domain.celublog.dto.request.AcceReqDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccDetailResDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccListResDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccResDto;
import com.hana.hana1pick.domain.celublog.service.CelublogService;
import com.hana.hana1pick.global.exception.BaseResponse;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/celub")
@Slf4j
public class CelublogController {
    private final CelublogService celublogService;

    @Operation(summary="셀럽로그 계좌 개설")
    @PostMapping("/accession")
    public SuccessResult<AccResDto> accedeCelublog(@RequestBody AcceReqDto req){
       return celublogService.accedeCelublog(req);
    }

    @Operation(summary="셀럽로그 계좌 리스트")
    @PostMapping("/account-list")
    public SuccessResult<AccListResDto> celubAccList(@RequestParam("userIdx") String userId){
        log.info("들어온다");
        UUID userIdx = UUID.fromString(userId);
        return celublogService.celubAccList(userIdx);
    }
    @Operation(summary="셀럽로그 계좌 상세 보기")
    @PostMapping("/list/detail")
    public SuccessResult<AccDetailResDto> celubAccDetail(@RequestParam("accountId") String accountId){
        log.info("성공");
        log.info(accountId);
        return celublogService.celubAccDetail(accountId);
    }
}
