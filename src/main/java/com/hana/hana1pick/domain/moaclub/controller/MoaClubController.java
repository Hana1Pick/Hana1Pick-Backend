package com.hana.hana1pick.domain.moaclub.controller;

import com.hana.hana1pick.domain.moaclub.dto.request.InviteMoaClubReqDto;
import com.hana.hana1pick.domain.moaclub.dto.request.OpenMoaClubReqDto;
import com.hana.hana1pick.domain.moaclub.dto.response.OpenMoaClubResDto;
import com.hana.hana1pick.domain.moaclub.service.MoaClubService;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moaclub")
public class MoaClubController {

    private final MoaClubService moaClubService;

    @Operation(summary = "모아클럽 개설")
    @PostMapping
    public SuccessResult<OpenMoaClubResDto> openMoaClub(@RequestBody OpenMoaClubReqDto request) {
        return moaClubService.openMoaClub(request);
    }

    @Operation(summary = "모아클럽 초대")
    @PostMapping("/invitations")
    public SuccessResult inviteMoaClub(@RequestBody InviteMoaClubReqDto request) {
        return moaClubService.inviteMoaClub(request);
    }
}
