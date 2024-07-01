package com.hana.hana1pick.domain.celublog.controller;

import com.hana.hana1pick.domain.celebrity.entity.CelubType;
import com.hana.hana1pick.domain.celublog.dto.request.AccInReqDto;
import com.hana.hana1pick.domain.celublog.dto.request.AcceReqDto;
import com.hana.hana1pick.domain.celublog.dto.request.AddRuleReqDto;
import com.hana.hana1pick.domain.celublog.dto.request.SearchReqDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccDetailResDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccListResDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccResDto;
import com.hana.hana1pick.domain.celublog.dto.response.AddRuleResDto;
import com.hana.hana1pick.domain.celublog.service.CelublogService;
import com.hana.hana1pick.domain.common.dto.request.CashOutReqDto;
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
        UUID userIdx = UUID.fromString(userId);
        return celublogService.celubAccList(userIdx);
    }
    @Operation(summary="셀럽로그 계좌 상세 보기")
    @PostMapping("/list/detail")
    public SuccessResult<AccDetailResDto> celubAccDetail(@RequestParam("accountId") String accountId){
        return celublogService.celubAccDetail(accountId);
    }

    @Operation(summary="셀럽로그 규칙 추가")
    @PostMapping("/rule")
    public SuccessResult<AddRuleResDto> celubAddRules(@RequestBody AddRuleReqDto req){
        return celublogService.celubAddRules(req);
    }

    @Operation(summary="셀럽로그 입금")
    @PostMapping("/in")
    public SuccessResult celubAddIn(@RequestBody AccInReqDto req){
        return celublogService.celubAddIn(req);
    }

    @Operation(summary = "셀럽로그 연예인 조회")
    @GetMapping("/list")
    public SuccessResult celubList(@RequestParam UUID userIdx){
        return celublogService.celubList(userIdx);
    }

    @Operation(summary = "셀럽로그 연예인 검색")
    @GetMapping("/list/search")
    public SuccessResult celubSearchList(@RequestParam("userIdx") UUID userIdx, @RequestParam("type") CelubType type, @RequestParam("name") String name){
        SearchReqDto req = new SearchReqDto(userIdx, type, name);

        return celublogService.celubSearchList(req);
    }

}
