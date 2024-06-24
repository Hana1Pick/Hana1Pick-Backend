package com.hana.hana1pick.domain.moaclub.service;

import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.moaclub.dto.request.*;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubOpeningResDto;
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

import java.util.*;

import static com.hana.hana1pick.domain.common.entity.AccountStatus.*;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubStatus.JOINED;
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

    public SuccessResult<ClubOpeningResDto> openMoaClub(ClubOpeningReqDto request) {
        // 예외처리
        User user = getUserByIdx(request.getUserIdx());
        openExceptionHandling(request, user);

        // 계좌번호 생성
        String accId = getAccId();

        // MoaClub 생성
        MoaClub moaClub = createMoaClub(request, user, accId);
        moaClubRepository.save(moaClub);
        user.getOwnerClubList().add(moaClub);

        // MoaClubMembers 생성
        createClubMembers(user, moaClub, user.getName(), request.getAccPw());

        return success(MOACLUB_CREATED_SUCCESS, new ClubOpeningResDto(accId));
    }

    public SuccessResult inviteMoaClub(ClubInvitationReqDto request) {
        MoaClub club = getClubByAccId(request.getAccountId());

        // 동명이인 처리
        List<String> uniqueNameList = assignUniqueNames(request.getInviteeList());

        // 초대 멤버 저장
        club.invite(uniqueNameList);

        return success(MOACLUB_INVITE_SUCCESS);
    }

    public SuccessResult joinMoaClub(ClubJoinReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 예외처리
        joinExceptionHandling(user, moaClub);

        // 초대 목록 상태 변경 및 동명이인 처리
        String uniqueName = generateUniqueName(user.getName(), moaClub);

        // 모아클럽 참여
        createClubMembers(user, moaClub, uniqueName, request.getAccPw());
        updateInviteeList(user, moaClub, uniqueName);

        return success(MOACLUB_JOIN_SUCCESS);
    }

    public SuccessResult updateMoaClub(ClubUpdateReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 개설자인지 확인
        validateFounder(user, moaClub);

        // 모아클럽 수정
        moaClubRepository.save(moaClub.update(request));

        return success(MOACLUB_UPDATE_SUCCESS);
    }

    private MoaClub createMoaClub(ClubOpeningReqDto request, User user, String accId) {
        return MoaClub.builder()
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
    }

    private void openExceptionHandling(ClubOpeningReqDto request, User user) {
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

    private void createClubMembers(User user, MoaClub club, String userName, String accPw) {
        ClubMembersId clubMembersId = new ClubMembersId(club.getAccountId(), user.getIdx());
        MoaClubMembers clubMembers = new MoaClubMembers(clubMembersId, club, user, userName, accPw);
        clubMembersRepository.save(clubMembers);

        user.getClubList().add(clubMembers);
        club.getClubMemberList().add(clubMembers);
        userRepository.save(user);
        moaClubRepository.save(club);
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

    private String generateUniqueName(String name, MoaClub moaClub) {
        long count = moaClub.getInviteeList().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(name) && entry.getValue() == JOINED)
                .count();

        if (count == 0) {
            return name;
        } else if (count == 1) {
            moaClub.getClubMemberList().stream()
                    .filter(member -> member.getUserName().equals(name))
                    .forEach(member -> member.updateUserName(name + 1));
            return name + 2;
        } else {
            return name + (count + 1);
        }
    }

    private void joinExceptionHandling(User user, MoaClub moaClub) {
        // 해지된 계좌인지 확인
        if (moaClub.getStatus().equals(INACTIVE)) {
            throw new BaseException(INACTIVE_MOACLUB);
        }

        // 초대받은 사용자인지 확인
        boolean hasPermission = false;

        for (String name : moaClub.getInviteeList().keySet()) {
            if (name.startsWith(user.getName())) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            throw new BaseException(NO_PERMISSION_TO_ACCESS_MOACLUB);
        }

        // 이미 가입한 클럽인지 확인
        for (MoaClubMembers clubMembers : user.getClubList()) {
            if (clubMembers.getClub().equals(moaClub)) {
                throw new BaseException(USER_ALREADY_JOINED);
            }
        }
    }

    private void updateInviteeList(User user, MoaClub moaClub, String uniqueName) {
        if (user.getName().equals(uniqueName)) {
            List<String> inviteeList = new ArrayList<>(moaClub.getInviteeList().keySet());
            long count = inviteeList.stream()
                    .filter(name -> name.startsWith(user.getName()))
                    .count();

            if (count >= 2) {
                uniqueName = user.getName() + 1;
            }
        }

        moaClub.getInviteeList().put(uniqueName, JOINED);
    }

    private void validateFounder(User user, MoaClub moaClub) {
        if (!moaClub.getUser().equals(user)) {
            throw new BaseException(NO_PERMISSION_TO_UPDATE);
        }

    }
}
