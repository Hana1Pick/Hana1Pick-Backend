package com.hana.hana1pick.domain.moaclub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.repository.AccHisRepository;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.moaclub.dto.request.*;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubFeeStatusResDto;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubOpeningResDto;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubResDto;
import com.hana.hana1pick.domain.moaclub.dto.response.ManagerChangeReq;
import com.hana.hana1pick.domain.moaclub.entity.*;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubMembersRepository;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hana.hana1pick.domain.common.entity.AccountStatus.*;
import static com.hana.hana1pick.domain.moaclub.dto.response.ClubFeeStatusResDto.ClubFeeStatus.PAID;
import static com.hana.hana1pick.domain.moaclub.dto.response.ClubFeeStatusResDto.ClubFeeStatus.UNPAID;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole.*;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubStatus.JOINED;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubStatus.PENDING;
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
    private final AccHisRepository accHisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic managerChangeTopic;

    private static final String MANAGER_CHANGE_KEY_PREFIX = "managerChangeRequest:";

    public SuccessResult<ClubOpeningResDto> openMoaClub(ClubOpeningReqDto request) {
        // 예외처리
        User user = getUserByIdx(request.getUserIdx());
        openExceptionHandling(request, user);

        // 계좌번호 생성
        String accId = getAccId();

        // MoaClub 생성
        MoaClub moaClub = createMoaClub(request, accId);
        moaClubRepository.save(moaClub);

        // MoaClubMembers 생성
        createClubMembers(user, moaClub, user.getName(), MANAGER);

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

    public SuccessResult joinMoaClub(AccIdReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 예외처리
        joinExceptionHandling(user, moaClub);

        // 동명이인 처리
        String uniqueName = generateUniqueName(user.getName(), moaClub);

        // 모아클럽 참여
        createClubMembers(user, moaClub, uniqueName, MEMBER);

        // 초대 목록 상태 변경
        updateInviteeList(user, moaClub);

        return success(MOACLUB_JOIN_SUCCESS);
    }

    public SuccessResult<ClubResDto> getMoaClub(AccIdReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 클럽 회원인지 확인
        validateClubMember(user, moaClub);

        // 클럽 회원 정보 저장
        List<ClubResDto.MoaClubMember> clubMemberList = getClubMemberList(moaClub);

        return success(MOACLUB_FETCH_SUCCESS, ClubResDto.of(moaClub, clubMemberList));
    }

    public SuccessResult updateMoaClub(ClubUpdateReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 관리자인지 확인
        validateManager(user, moaClub);

        // 모아클럽 수정
        moaClubRepository.save(moaClub.update(request));

        return success(MOACLUB_UPDATE_SUCCESS);
    }

    public SuccessResult<List<ClubFeeStatusResDto>> getMoaClubFeeStatus(ClubFeeStatusReqDto request) {
        MoaClub moaClub = getClubByAccId(request.getAccountId());
        List<ClubFeeStatusResDto> clubFeeStatus = new ArrayList<>();

        for (MoaClubMembers member : moaClub.getClubMemberList()) {
            if (member.getRole() != NONMEMBER) {
                clubFeeStatus.add(getMemberFeeStatus(member, moaClub, request));
            }
        }

        return success(MOACLUB_FEE_STATUS_FETCH_SUCCESS, clubFeeStatus);
    }

    public SuccessResult leaveMoaClub(ClubMemberLeaveReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        MoaClubMembers clubMember = getClubMemberByUserAndClub(user, moaClub);

        // 탈퇴하려는 클럽멤버가 관리자인지 확인
        if (clubMember.getRole() == MANAGER) {
            // 클럽 멤버가 남아있는지 확인
            checkRemainingMembers(moaClub);

            // 관리자 입출금 통장으로 전액 입금 후 모아클럽 해지
            // 입금 - 추후 개발 예정
            clubMember.updateUserRole(NONMEMBER);
            moaClub.closeAccount();
        } else {
            clubMember.updateUserRole(NONMEMBER);
        }

        return success(MOACLUB_MEMBER_LEAVE_SUCCESS);
    }

    public SuccessResult requestManagerChange(ClubManagerChangeReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());
        User candidate = getUserByIdx(request.getCandidateIdx());

        // 관리자인지 확인
        validateManager(user, moaClub);

        // 후보 관리자가 클럽멤버인지 확인
        MoaClubMembers memberUser = getClubMemberByUserAndClub(user, moaClub);
        MoaClubMembers memberCandidate = getClubMemberByUserAndClub(candidate, moaClub);

        // Redis key 설정
        String key = MANAGER_CHANGE_KEY_PREFIX + request.getAccountId();

        // 변경 요청이 이미 존재하는 경우
        if (redisTemplate.hasKey(key)) {
            throw new BaseException(REQUEST_ALREADY_PENDING);
        }

        ManagerChangeReq changeReq = new ManagerChangeReq(
                moaClub.getAccountId(), memberUser.getUserName(), memberCandidate.getUserName(), LocalDateTime.now(), new HashMap<>());
        redisTemplate.opsForValue().set(key, changeReq, Duration.ofHours(24));

        // 관리자 제외 클럽멤버에게 실시간 알림 발송 - 추후 개발 예정

        return success(MOACLUB_MANAGER_REQUEST_SUCCESS);
    }

    public SuccessResult voteMoaClubRequest(ClubVoteReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 관리자가 아닌 클럽 멤버인지 확인
        MoaClubMembers member = getClubMemberByUserAndClub(user, moaClub);
        if (member.getRole() == MANAGER) {
            throw new BaseException(NO_PERMISSION_TO_VOTE);
        }

        // Redis key 설정
        String key = MANAGER_CHANGE_KEY_PREFIX + request.getAccountId();

        ManagerChangeReq changeReq = getRequest(key);

        // 요청 없는 경우 예외 처리
        if (changeReq == null) {
            throw new BaseException(MOACLUB_REQUEST_NOT_FOUND);
        }

        // 투표 결과 저장
        changeReq.getVotes().put(member.getUserName(), request.getAgree());
        redisTemplate.opsForValue().set(key, changeReq);

        // Redis Pub
        redisTemplate.convertAndSend(managerChangeTopic.getTopic(), changeReq);

        return success(MOACLUB_VOTE_SUCCESS);
    }

    public SuccessResult<ManagerChangeReq> getMoaClubRequest(int type, AccIdReqDto request) {
        // Redis key 설정
        String key = MANAGER_CHANGE_KEY_PREFIX + request.getAccountId();

        ManagerChangeReq changeReq = getRequest(key);

        // 요청 없는 경우 예외 처리
        if (changeReq == null) {
            throw new BaseException(MOACLUB_REQUEST_NOT_FOUND);
        }

        return success(MOACLUB_REQUEST_FETCH_SUCCESS, changeReq);
    }

    private MoaClub createMoaClub(ClubOpeningReqDto request, String accId) {
        return MoaClub.builder()
                .balance(0L)
                .status(ACTIVE)
                .accountId(accId)
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

    private void createClubMembers(User user, MoaClub club, String userName, MoaClubMemberRole role) {
        ClubMembersId clubMembersId = new ClubMembersId(club.getAccountId(), user.getIdx());
        MoaClubMembers clubMembers = new MoaClubMembers(clubMembersId, club, user, userName, role);
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
        long count = moaClub.getClubMemberList().stream()
                .filter(member -> member.getUser().getName().equals(name))
                .count();

        if (getFounderName(moaClub.getClubMemberList()).equals(name)) {
            moaClub.getClubMemberList().stream()
                    .filter(member -> member.getRole().equals(MANAGER))
                    .forEach(member -> member.updateUserName(name + 1));

            count = 1;
        }

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

    private String getFounderName(List<MoaClubMembers> clubMemberList) {
        Optional<MoaClubMembers> founder = clubMemberList.stream()
                .filter(member -> member.getRole() == MANAGER)
                .findFirst();

        return founder.map(MoaClubMembers::getUserName).orElse(null);
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

    private void updateInviteeList(User user, MoaClub moaClub) {
        moaClub.getInviteeList().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(user.getName()) && entry.getValue() == PENDING)
                .findFirst()
                .ifPresent(entry -> moaClub.getInviteeList().put(entry.getKey(), JOINED));
    }

    private void validateManager(User user, MoaClub moaClub) {
        MoaClubMembers member = getClubMemberByUserAndClub(user, moaClub);

        if (member.getRole() != MANAGER) {
            throw new BaseException(NO_PERMISSION_TO_MANAGE);
        }
    }

    private void validateClubMember(User user, MoaClub moaClub) {
        boolean isClubMember = moaClub.getClubMemberList().stream()
                .anyMatch(clubMembers -> clubMembers.getUser().equals(user));

        boolean isNonMember = moaClub.getClubMemberList().stream()
                .anyMatch(clubMembers -> clubMembers.getUser().equals(user)
                        && clubMembers.getRole() == MoaClubMemberRole.NONMEMBER);

        if (!isClubMember || isNonMember) {
            throw new BaseException(USER_NOT_CLUB_MEMBER);
        }
    }

    private List<ClubResDto.MoaClubMember> getClubMemberList(MoaClub moaClub) {
        return moaClub.getClubMemberList().stream()
                .filter(member -> member.getRole() != NONMEMBER)
                .map(member -> ClubResDto.MoaClubMember.from(member))
                .collect(Collectors.toList());
    }

    private ClubFeeStatusResDto getMemberFeeStatus(MoaClubMembers member, MoaClub moaClub, ClubFeeStatusReqDto request) {
        User user = member.getUser();
        Deposit deposit = user.getDeposit();

        List<AccountHistory> accHisList = accHisRepository.findClubFeeHistory(
                deposit.getAccountId(),
                moaClub.getAccountId(),
                request.getCheckDate().getYear(),
                request.getCheckDate().getMonthValue()
        );

        if (!accHisList.isEmpty()) {
            Long totalAmount = accHisList.stream().mapToLong(AccountHistory::getTransAmount).sum();
            return new ClubFeeStatusResDto(member.getUserName(), totalAmount, PAID);
        } else {
            return new ClubFeeStatusResDto(member.getUserName(), 0L, UNPAID);
        }
    }

    private MoaClubMembers getClubMemberByUserAndClub(User user, MoaClub moaClub) {
        ClubMembersId clubMembersId = new ClubMembersId(moaClub.getAccountId(), user.getIdx());
        return clubMembersRepository.findById(clubMembersId)
                .orElseThrow(() -> new BaseException(MOACLUB_MEMBER_NOT_FOUND));
    }

    private void checkRemainingMembers(MoaClub moaClub) {
        boolean hasMember = moaClub.getClubMemberList().stream()
                .anyMatch(member -> member.getRole() == MEMBER);

        if (hasMember) {
            throw new BaseException(MOACLUB_HAS_MEMBER);
        }
    }

    private ManagerChangeReq getRequest(String key) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.registerModule(new JavaTimeModule())
                .convertValue(redisTemplate.opsForValue().get(key), ManagerChangeReq.class);
    }
}
