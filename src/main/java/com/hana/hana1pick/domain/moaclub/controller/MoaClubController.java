package com.hana.hana1pick.domain.moaclub.controller;

import com.hana.hana1pick.domain.moaclub.dto.request.*;
import com.hana.hana1pick.domain.moaclub.dto.response.AccPwCheckResDto;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubOpeningResDto;
import com.hana.hana1pick.domain.moaclub.service.MoaClubService;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moaclub")
public class MoaClubController {

    private final MoaClubService moaClubService;

    @Operation(summary = "모아클럽 개설")
    @PostMapping
    public SuccessResult<ClubOpeningResDto> openMoaClub(@RequestBody ClubOpeningReqDto request) {
        return moaClubService.openMoaClub(request);
    }

    @Operation(summary = "모아클럽 초대")
    @PostMapping("/invitations")
    public SuccessResult inviteMoaClub(@RequestBody ClubInvitationReqDto request) {
        return moaClubService.inviteMoaClub(request);
    }

    @Operation(summary = "모아클럽 가입")
    @PostMapping("/admission")
    public SuccessResult joinMoaClub(@RequestBody ClubPwReqDto request) {
        return moaClubService.joinMoaClub(request);
    }

    @Operation(summary = "계좌 비밀번호 확인")
    @PostMapping("/password-check")
    public SuccessResult<AccPwCheckResDto> checkAccPw(@RequestBody ClubPwReqDto request) {
        return moaClubService.checkAccPw(request);
    }

    @Operation(summary = "모아클럽 수정")
    @PutMapping
    public SuccessResult updateMoaClub(@RequestBody ClubUpdateReqDto request) {
        return moaClubService.updateMoaClub(request);
    }

    @Operation(summary = "모아클럽 회원 비밀번호 수정")
    @PutMapping("/member")
    public SuccessResult updateMoaClubMemberPw(@RequestBody ClubPwReqDto request) {
        return moaClubService.updateMoaClubMemberPw(request);
    }
}
