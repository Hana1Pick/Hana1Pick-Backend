package com.hana.hana1pick.domain.moaclub.service;

import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.moaclub.dto.request.InviteMoaClubReqDto;
import com.hana.hana1pick.domain.moaclub.dto.request.OpenMoaClubReqDto;
import com.hana.hana1pick.domain.moaclub.dto.response.OpenMoaClubResDto;
import com.hana.hana1pick.domain.moaclub.entity.ClubMembersId;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubMembersRepository;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.hana.hana1pick.domain.common.entity.AccountStatus.*;
import static com.hana.hana1pick.global.exception.BaseResponse.*;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MoaClubService {

    private final MoaClubRepository moaClubRepository;
    private final MoaClubMembersRepository clubMembersRepository;
    private final UserRepository userRepository;
    private final DepositRepository depositRepository;
    private final AccIdGenerator accIdGenerator;
    private final BCryptPasswordEncoder passwordEncoder;

    public SuccessResult<OpenMoaClubResDto> openMoaClub(OpenMoaClubReqDto request) {
        // 예외처리
        User user = getUserByIdx(request.getUserIdx());
        exceptionHandling(request, user);

        // 계좌번호 생성
        String accId = getAccId();

        // MoaClub 생성
        MoaClub moaClub = MoaClub.builder()
                .accPw(passwordEncoder.encode(request.getAccPw()))
                .balance(0L)
                .status(ACTIVE)
                .accountId(accId)
                .user(user)
                .name(request.getName())
                .clubFee(request.getClubFee())
                .atDate(request.getAtDate())
                .currency(request.getCurrency())
                .build();
        moaClubRepository.save(moaClub);
        user.getOwnerClubList().add(moaClub);

        // MoaClubMembers 생성
        createClubMembers(user, moaClub, user.getName());

        return success(MOACLUB_CREATED_SUCCESS, new OpenMoaClubResDto(accId));
    }

    public SuccessResult inviteMoaClub(InviteMoaClubReqDto request) {
        MoaClub club = getClubByAccId(request.getAccountId());

        // 동명이인 처리
        List<String> uniqueNameList = assignUniqueNames(request.getInviteeList());

        // 초대 멤버 저장
        club.invite(uniqueNameList);

        return success(MOACLUB_INVITE_SUCCESS);
    }

    private void exceptionHandling(OpenMoaClubReqDto request, User user) {
        // 출금계좌가 사용자 소유 계좌가 아닌 경우
        Deposit outAcc = getDepositByAccId(request.getAccountId());

        if (!user.getDeposit().equals(outAcc)) {
            throw new BaseException(NOT_ACCOUNT_OWNER);
        }

        // 회비 금액이 잘못된 경우
        if (request.getClubFee() < 0) {
            throw new BaseException(INVALID_TRANSFER_AMOUNT);
        }
    }

    private User getUserByIdx(UUID userIdx) {
        return userRepository.findById(userIdx)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    private Deposit getDepositByAccId(String accId) {
        return depositRepository.findById(accId)
                .orElseThrow(() -> new BaseException(DEPOSIT_NOT_FOUND));
    }

    private String getAccId() {
        String accId;
        do {
            accId = accIdGenerator.generateMoaClubAccId();
        } while (moaClubRepository.existsById(accId));

        return accId;
    }

    private void createClubMembers(User user, MoaClub club, String userName) {
        ClubMembersId clubMembersId = new ClubMembersId(club.getAccountId(), user.getIdx());
        MoaClubMembers clubMembers = new MoaClubMembers(clubMembersId, club, user, userName);
        clubMembersRepository.save(clubMembers);

        user.getMemberClubList().add(clubMembers);
        club.getClubMemberList().add(clubMembers);
    }

    private MoaClub getClubByAccId(String accId) {
        return moaClubRepository.findById(accId)
                .orElseThrow(() -> new BaseException(MOACLUB_NOT_FOUND));
    }

    private List<String> assignUniqueNames(List<String> inviteeList) {
        List<String> uniqueNameList = new ArrayList<>();

        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String name : inviteeList) {
            frequencyMap.put(name, frequencyMap.getOrDefault(name, 0) + 1);
        }

        Map<String, Integer> countMap = new HashMap<>();
        for (String name : inviteeList) {
            int count = countMap.getOrDefault(name, 0) + 1;
            countMap.put(name, count);
            if (frequencyMap.get(name) > 1) {
                uniqueNameList.add(name + count);
            } else {
                uniqueNameList.add(name);
            }
        }

        return uniqueNameList;
    }
}
