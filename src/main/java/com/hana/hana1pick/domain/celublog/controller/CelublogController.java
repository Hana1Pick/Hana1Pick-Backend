package com.hana.hana1pick.domain.celublog.controller;

import com.hana.hana1pick.domain.celublog.dto.request.AcceReqDto;
import com.hana.hana1pick.domain.celublog.dto.response.AccResDto;
import com.hana.hana1pick.domain.celublog.service.CelublogService;
import com.hana.hana1pick.global.exception.BaseResponse;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/celub")
public class CelublogController {
    private final CelublogService celublogService;

//    public BaseResponse.SuccessResult getCelubList(){
//        return celublogService.getCelubList;
//    }

    @Operation(summary="셀럽로그 계좌 개설")
    @PostMapping("/accession")
    public SuccessResult<AccResDto> accedeCelublog(){
//        @RequestBody AcceReqDto req
        AcceReqDto req = AcceReqDto.builder().accPw("1234").outAccId("02-00-0000507").name("김가원").celebrityIdx(Long.parseLong("1")).userIdx(UUID.fromString("123e4567-e89b-12d3-a456-556655440000")).imgSrc("imgimgimg").build();
       return celublogService.accedeCelublog(req);
    }

}
