package com.hana.hana1pick.domain.moaclub.controller;

import com.hana.hana1pick.domain.moaclub.dto.request.*;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubFeeStatusResDto;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubOpeningResDto;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubResDto;
import com.hana.hana1pick.domain.moaclub.service.MoaClubService;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public SuccessResult joinMoaClub(@RequestBody AccIdReqDto request) {
        return moaClubService.joinMoaClub(request);
    }

    @Operation(summary = "모아클럽 조회")
    @PostMapping("/info")
    public SuccessResult<ClubResDto> getMoaClub(@RequestBody AccIdReqDto request) {
        return moaClubService.getMoaClub(request);
    }

    @Operation(summary = "모아클럽 수정")
    @PutMapping
    public SuccessResult updateMoaClub(@RequestBody ClubUpdateReqDto request) {
        return moaClubService.updateMoaClub(request);
    }

    @Operation(summary = "모아클럽 입금현황 조회")
    @PostMapping("/fee")
    public SuccessResult<List<ClubFeeStatusResDto>> getMoaClubFeeStatus(@RequestBody ClubFeeStatusReqDto request) {
        return moaClubService.getMoaClubFeeStatus(request);
    }

    @Operation(summary = "모아클럽 탈퇴")
    @DeleteMapping
    public SuccessResult leaveMoaClub(@RequestBody ClubMemberLeaveReqDto request) {
        return moaClubService.leaveMoaClub(request);
    }

    @Operation(summary = "모아클럽 관리자 변경 요청")
    @PostMapping("/manager/request")
    public SuccessResult requestManagerChange(@RequestBody ClubManagerChangeReqDto request) {
        return moaClubService.requestManagerChange(request);
    }
}
