package com.hana.hana1pick.domain.moaclub.controller;

import com.hana.hana1pick.domain.moaclub.dto.request.*;
import com.hana.hana1pick.domain.moaclub.dto.response.*;
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

    @Operation(summary = "모아클럽 가입 정보")
    @GetMapping("/admission-info")
    public SuccessResult<ClubInfoResDto> getMoaClubInfo(@RequestParam("accountId") String accountId) {
        return moaClubService.getMoaClubInfo(accountId);
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

    @Operation(summary = "모아클럽 관리자 조회")
    @PostMapping("/manager-check")
    public SuccessResult<ClubManagerCheckResDto> checkMoaClubManager(@RequestBody AccIdReqDto request) {
        return moaClubService.checkMoaClubManager(request);
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
    @PostMapping("/request-manager")
    public SuccessResult requestManagerChange(@RequestBody ClubManagerChangeReqDto request) {
        return moaClubService.requestManagerChange(request);
    }

    @Operation(summary = "모아클럽 출금 요청")
    @PostMapping("/request-withdraw")
    public SuccessResult requestWithdraw(@RequestBody ClubWithdrawReqDto request) {
        return moaClubService.requestWithdraw(request);
    }

    @Operation(summary = "모아클럽 요청 조회")
    @PostMapping("/vote-result")
    public SuccessResult<VoteResult> getMoaClubRequest(@RequestParam(name = "type") int type, @RequestBody AccIdReqDto request) {
        return moaClubService.getMoaClubRequest(type, request);
    }

    @Operation(summary = "모아클럽 요청 투표")
    @PostMapping("/vote")
    public SuccessResult voteMoaClubRequest(@RequestParam(name = "type") int type, @RequestBody ClubVoteReqDto request) {
        return moaClubService.voteMoaClubRequest(type, request);
    }

    @Operation(summary = "모아클럽 멤버 리스트 조회")
    @GetMapping("/member")
    public SuccessResult<List<ClubResDto.MoaClubMember>> getMoaClubMemberList(@RequestParam(name = "accountId") String accountId) {
        return moaClubService.getMoaClubMemberList(accountId);
    }

    @Operation(summary = "모아클럽 자동이체 설정")
    @PostMapping("/auto-transfer/setting")
    public SuccessResult registerAutoTransfer(@RequestBody ClubAutoTransferReqDto request) {
        return moaClubService.registerAutoTransfer(request);
    }

    @Operation(summary = "모아클럽 자동이체 조회")
    @PostMapping("/auto-transfer")
    public SuccessResult<ClubAutoTransferResDto> getAutoTransfer(@RequestBody AccIdReqDto request) {
        return moaClubService.getAutoTransfer(request);
    }

    @Operation(summary = "모아클럽 자동이체 삭제")
    @DeleteMapping("/auto-transfer")
    public SuccessResult deleteAutoTransfer(@RequestBody ClubAutoTransferReqDto request) {
        return moaClubService.deleteAutoTransfer(request);
    }
}
