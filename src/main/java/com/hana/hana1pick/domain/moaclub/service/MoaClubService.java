package com.hana.hana1pick.domain.moaclub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.acchistory.repository.AccHistoryRepository;
import com.hana.hana1pick.domain.autotranfer.entity.AutoTransfer;
import com.hana.hana1pick.domain.autotranfer.entity.AutoTransferId;
import com.hana.hana1pick.domain.autotranfer.repository.AutoTransferRepository;
import com.hana.hana1pick.domain.autotranfer.service.AutoTransferService;
import com.hana.hana1pick.domain.chat.entity.ChatRoom;
import com.hana.hana1pick.domain.chat.repository.ChatRoomRepository;
import com.hana.hana1pick.domain.chat.service.ChatRoomService;
import com.hana.hana1pick.domain.common.dto.request.CashOutReqDto;
import com.hana.hana1pick.domain.common.service.AccIdGenerator;
import com.hana.hana1pick.domain.common.service.AccountService;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.moaclub.dto.request.*;
import com.hana.hana1pick.domain.moaclub.dto.response.*;
import com.hana.hana1pick.domain.moaclub.entity.*;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubMembersRepository;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.domain.notification.entity.NotificationType;
import com.hana.hana1pick.domain.notification.service.NotificationService;
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

import static com.hana.hana1pick.domain.acchistory.entity.TransType.AUTO_TRANSFER;
import static com.hana.hana1pick.domain.common.entity.AccountStatus.*;
import static com.hana.hana1pick.domain.moaclub.dto.response.ClubFeeStatusResDto.ClubFeeStatus.PAID;
import static com.hana.hana1pick.domain.moaclub.dto.response.ClubFeeStatusResDto.ClubFeeStatus.UNPAID;
import static com.hana.hana1pick.domain.moaclub.entity.Currency.KRW;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole.*;
import static com.hana.hana1pick.domain.notification.entity.NotificationType.VOTE;
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
    private final AccHistoryRepository accHisRepository;
    private final AccountService accountService;
    private final AutoTransferRepository autoTransferRepository;
    private final AutoTransferService autoTransferService;
    private final NotificationService notificationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic managerChangeTopic;
    private final ChannelTopic withdrawTopic;
    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;

    private static final String MANAGER_CHANGE_KEY_PREFIX = "managerChangeRequest:";
    private static final String WITHDRAW_KEY_PREFIX = "withdrawRequest:";

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

        // ChatRoom 생성
        ChatRoom chatRoom = chatRoomService.createChatRoom(moaClub);
        chatRoomRepository.save(chatRoom);

        return success(MOACLUB_CREATED_SUCCESS, new ClubOpeningResDto(accId));
    }

    public SuccessResult<ClubInfoResDto> getMoaClubInfo(String accountId) {
        MoaClub moaClub = getClubByAccId(accountId);
        String managerName = getFounderName(moaClub.getClubMemberList());

        return success(MOACLUB_FETCH_SUCCESS, new ClubInfoResDto(managerName, moaClub.getName()));
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

        return success(MOACLUB_JOIN_SUCCESS);
    }

    public SuccessResult<ClubResDto> getMoaClub(AccIdReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 클럽 회원인지 확인
        validateClubMember(user, moaClub);

        // 클럽 회원 정보 저장
        List<ClubResDto.MoaClubMember> clubMemberList = getClubMemberListExceptNonmember(moaClub);

        // 클럽 채팅방 정보 저장
        Long chatRoomId = moaClub.getChatRoom().getChatRoomId();

        return success(MOACLUB_FETCH_SUCCESS, ClubResDto.of(moaClub, clubMemberList, chatRoomId));
    }

    public SuccessResult<ClubManagerCheckResDto> checkMoaClubManager(AccIdReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());
        MoaClubMembers member = getClubMemberByUserAndClub(user, moaClub);

        boolean check = (member.getRole() == MANAGER);

        return success(MOACLUB_MANAGER_CHECK_SUCCESS, new ClubManagerCheckResDto(check));
    }

    public SuccessResult updateMoaClub(ClubUpdateReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 관리자인지 확인
        validateManager(user, moaClub);

        // 자동이체 수정
        if (request.getAtDate() != moaClub.getAtDate() || request.getClubFee() != moaClub.getClubFee()) {
            autoTransferService.updateAutoTrsfByInAccId(moaClub.getAccountId(), request.getAtDate(), request.getClubFee());
        }

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

            // 관리자 입출금 통장으로 전액 입금
            fullTransfer(moaClub, clubMember);
            // 클럽에 연결된 모든 자동이체 삭제
            deleteAutoTransfer(moaClub);
            // 관리자 탈퇴 및 모아클럽 해지
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

        // 관리자 제외 클럽멤버에게 실시간 알림 발송
        sendNotification(moaClub, user, "관리자 변경", "manager");

        return success(MOACLUB_REQUEST_SUCCESS);
    }

    public SuccessResult requestWithdraw(ClubWithdrawReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());
        MoaClubMembers member = getClubMemberByUserAndClub(user, moaClub);

        // 관리자인지 확인
        validateManager(user, moaClub);

        // 유효한 출금 금액인지 확인
        if (request.getAmount() > moaClub.getBalance()) {
            throw new BaseException(INVALID_TRANSFER_AMOUNT);
        }

        // Redis key 설정
        String key = WITHDRAW_KEY_PREFIX + request.getAccountId();

        // 변경 요청이 이미 존재하는 경우
        if (redisTemplate.hasKey(key)) {
            throw new BaseException(REQUEST_ALREADY_PENDING);
        }

        WithdrawReq changeReq = new WithdrawReq(
                moaClub.getAccountId(), member.getUserName(), request.getAmount(), LocalDateTime.now(), new HashMap<>()
        );
        redisTemplate.opsForValue().set(key, changeReq, Duration.ofHours(24));

        // 관리자 제외 클럽멤버에게 실시간 알림 발송
        sendNotification(moaClub, user, "출금", "trsf");

        return success(MOACLUB_REQUEST_SUCCESS);
    }

    public SuccessResult<VoteResult> getMoaClubRequest(int type, AccIdReqDto request) {
        // Redis key 설정
        String key = getRedisKey(type) + request.getAccountId();

        // 결과 반환
        VoteResult voteResult = getVoteResult(type, key);

        return success(MOACLUB_REQUEST_FETCH_SUCCESS, voteResult);
    }

    public SuccessResult voteMoaClubRequest(int type, ClubVoteReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getAccountId());

        // 관리자가 아닌 클럽 멤버인지 확인
        MoaClubMembers member = getClubMemberByUserAndClub(user, moaClub);
        validateMember(member);

        // Redis key 설정
        String key = getRedisKey(type) + request.getAccountId();

        // 결과 반환
        VoteResult voteResult = getVoteResult(type, key);

        // 투표 결과 저장
        voteResult.getVotes().put(member.getUserName(), request.getAgree());
        redisTemplate.opsForValue().set(key, voteResult);

        // Redis Pub
        if (type == 0) {
            redisTemplate.convertAndSend(managerChangeTopic.getTopic(), voteResult);
        } else {
            redisTemplate.convertAndSend(withdrawTopic.getTopic(), voteResult);
        }

        return success(MOACLUB_VOTE_SUCCESS);
    }

    public SuccessResult<List<ClubResDto.MoaClubMember>> getMoaClubMemberList(String accountId) {
        MoaClub moaClub = getClubByAccId(accountId);
        List<MoaClubMembers> memberList = moaClub.getClubMemberList();

        List<ClubResDto.MoaClubMember> result = new ArrayList<>();
        for (MoaClubMembers member : memberList) {
            result.add(ClubResDto.MoaClubMember.from(member));
        }

        return success(MOACLUB_MEMBER_FETCH_SUCCESS, result);
    }

    public SuccessResult registerAutoTransfer(ClubAutoTransferReqDto request) {
        AutoTransfer autoTransfer = createAutoTransfer(request);
        autoTransferRepository.save(autoTransfer);

        return success(MOACLUB_AUTO_TRANSFER_SET_SUCCESS);
    }

    public SuccessResult<ClubAutoTransferResDto> getAutoTransfer(AccIdReqDto request) {
        User user = getUserByIdx(request.getUserIdx());

        AutoTransfer autoTransfer = autoTransferRepository.findByInAccIdAndOutAccId(request.getAccountId(), user.getDeposit().getAccountId())
                .orElseThrow(() -> new BaseException(AUTO_TRANSFER_NOT_FOUND));

        return success(MOACLUB_AUTO_TRANSFER_FETCH_SUCCESS, ClubAutoTransferResDto.from(autoTransfer));
    }

    public SuccessResult deleteAutoTransfer(ClubAutoTransferReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getInAccId());

        validateClubMember(user, moaClub);

        autoTransferService.deleteAutoTrsfByInAccIdAndOutAccId(request.getInAccId(), request.getOutAccId());
        return success(AUTO_TRANSFER_DELETE_SUCCESS);
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
                .orElseThrow(() -> new BaseException(ACCOUNT_NOT_FOUND));
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

        // 이미 가입한 클럽인지 확인
        for (MoaClubMembers clubMembers : user.getClubList()) {
            if (clubMembers.getClub().equals(moaClub)) {
                throw new BaseException(USER_ALREADY_JOINED);
            }
        }
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

    private List<ClubResDto.MoaClubMember> getClubMemberListExceptNonmember(MoaClub moaClub) {
        return moaClub.getClubMemberList().stream()
                .filter(member -> member.getRole() != NONMEMBER)
                .map(member -> ClubResDto.MoaClubMember.from(member))
                .collect(Collectors.toList());
    }

    private ClubFeeStatusResDto getMemberFeeStatus(MoaClubMembers member, MoaClub moaClub, ClubFeeStatusReqDto request) {
        User user = member.getUser();
        Deposit deposit = user.getDeposit();

        boolean isFx = !moaClub.getCurrency().equals(KRW);

        List<AccountHistory> accHisList = accHisRepository.findClubFeeHistory(
                deposit.getAccountId(),
                moaClub.getAccountId(),
                request.getCheckDate().getYear(),
                request.getCheckDate().getMonthValue(),
                isFx
        );

        if (!accHisList.isEmpty()) {
            Long totalAmount = accHisList.stream().mapToLong(AccountHistory::getTransAmount).sum();
            return new ClubFeeStatusResDto(member.getUserName(), user.getProfile(), totalAmount, PAID);
        } else {
            return new ClubFeeStatusResDto(member.getUserName(), user.getProfile(),0L, UNPAID);
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

    private void fullTransfer(MoaClub moaClub, MoaClubMembers clubMember) {
        User manager = clubMember.getUser();
        Deposit managerAcc = manager.getDeposit();

        // 이체 DTO 생성
        boolean isFx = !moaClub.getCurrency().equals(KRW);
        CashOutReqDto transfer = CashOutReqDto.of(moaClub.getAccountId(), managerAcc.getAccountId(), moaClub.getBalance(), AUTO_TRANSFER, moaClub.getCurrency());

        // 이체
        accountService.cashOut(transfer);
    }

    private void validateMember(MoaClubMembers member) {
        if (member.getRole() == MANAGER) {
            throw new BaseException(NO_PERMISSION_TO_VOTE);
        }
    }

    private String getRedisKey(int type) {
        String key = type == 0 ? MANAGER_CHANGE_KEY_PREFIX : WITHDRAW_KEY_PREFIX;
        return key;
    }

    private VoteResult getVoteResult(int type, String key) {
        VoteResult voteResult = type == 0 ? getRequest(key, ManagerChangeReq.class) : getRequest(key, WithdrawReq.class);

        // 요청 없는 경우 예외 처리
        if (voteResult == null) {
            throw new BaseException(MOACLUB_REQUEST_NOT_FOUND);
        }

        return voteResult;
    }

    private <T extends VoteResult> T getRequest(String key, Class<T> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.registerModule(new JavaTimeModule())
                .convertValue(redisTemplate.opsForValue().get(key), tClass);
    }

    private AutoTransfer createAutoTransfer(ClubAutoTransferReqDto request) {
        User user = getUserByIdx(request.getUserIdx());
        MoaClub moaClub = getClubByAccId(request.getInAccId());

        // 예외처리
        autoTransferExceptionHandling(user, moaClub, request.getOutAccId());

        // 자동이체 생성
        AutoTransferId autoTransferId = AutoTransferId.builder()
                .atDate(moaClub.getAtDate())
                .inAccId(request.getInAccId())
                .outAccId(request.getOutAccId())
                .build();

        AutoTransfer autoTransfer = AutoTransfer.builder()
                .id(autoTransferId)
                .amount(moaClub.getClubFee())
                .outAcc(user.getDeposit())
                .currency(moaClub.getCurrency())
                .build();

        return autoTransfer;
    }

    private void autoTransferExceptionHandling(User user, MoaClub moaClub, String outAccId) {
        // 클럽멤버인지 확인
        validateClubMember(user, moaClub);

        // 출금계좌가 본인 계좌인지 확인
        if (!user.getDeposit().getAccountId().equals(outAccId)) {
            throw new BaseException(NOT_ACCOUNT_OWNER);
        }
    }

    private void deleteAutoTransfer(MoaClub moaClub) {
        autoTransferService.deleteAutoTrsfByInAccId(moaClub.getAccountId());
    }

    private void sendNotification(MoaClub moaClub, User user, String action, String endpoint) {
        List<User> clubMembers = getClubMemberUser(moaClub);

        String content = "[모아클럽🗳️]\n" + "'" + moaClub.getName() + "'에서 " + user.getName() + "님이 " + action + "을 요청했어요. 지금 바로 투표해 보세요️!";
        String url = "/moaclub/vote/" + endpoint + "/" + moaClub.getAccountId();

        for (User member : clubMembers) {
            notificationService.send(member, content, url, VOTE);
        }
    }

    private List<User> getClubMemberUser(MoaClub moaClub) {
        List<User> clubMembers = new ArrayList<>();

        for (MoaClubMembers member : moaClub.getClubMemberList()) {
            if (member.getRole() == MEMBER) {
                clubMembers.add(member.getUser());
            }
        }

        return clubMembers;
    }
}
