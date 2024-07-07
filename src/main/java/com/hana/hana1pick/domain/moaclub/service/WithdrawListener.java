package com.hana.hana1pick.domain.moaclub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.hana1pick.domain.common.dto.request.CashOutReqDto;
import com.hana.hana1pick.domain.common.service.AccountService;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.moaclub.dto.response.WithdrawReq;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubMembersRepository;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.domain.notification.entity.NotificationType;
import com.hana.hana1pick.domain.notification.service.NotificationService;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.hana.hana1pick.domain.acchistory.entity.TransType.AUTO_TRANSFER;
import static com.hana.hana1pick.domain.moaclub.entity.Currency.*;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole.MEMBER;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole.NONMEMBER;
import static com.hana.hana1pick.domain.notification.entity.NotificationType.ALARM;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.MOACLUB_MEMBER_NOT_FOUND;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.MOACLUB_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawListener implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MoaClubRepository moaClubRepository;
    private final MoaClubMembersRepository clubMembersRepository;
    private final AccountService accountService;
    private final NotificationService notificationService;

    private static final String WITHDRAW_KEY_PREFIX = "withdrawRequest:";

    /**
     * Callback for processing received objects through Redis.
     *
     * @param message message must not be {@literal null}.
     * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String pubMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());

            WithdrawReq request = objectMapper.readValue(pubMessage, WithdrawReq.class);

            // Redis key 설정
            String key = WITHDRAW_KEY_PREFIX + request.getAccountId();

            int memberCount = getClubMemberCount(request.getAccountId());

            if (request.getVotes().containsValue(false)) {
                redisTemplate.delete(key);

                // 클럽멤버 모두에게 출금 요청 취소 알림
                notificateWithdrawRequest(request, "출금 요청이 거절되었어요.");
            } else if (request.getVotes().size() == memberCount && !request.getVotes().containsValue(false)) {
                // 출금
                transfer(request);

                // 투표 삭제
                redisTemplate.delete(key);

                // 클럽멤버 모두에게 출금 알림
                notificateWithdrawRequest(request, request.getAmount() + getCurrencyValue(request.getAccountId()) + " 출금");
            }

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void transfer(WithdrawReq request) {
        MoaClub moaClub = getClubByAccId(request.getAccountId());
        User manager = getClubMemberByClubAndUserName(moaClub, request.getUserName()).getUser();
        Deposit managerAcc = manager.getDeposit();

        // 이체 DTO 생성
        CashOutReqDto transfer = CashOutReqDto.of(moaClub.getAccountId(), managerAcc.getAccountId(), request.getAmount(), AUTO_TRANSFER, moaClub.getCurrency());
        accountService.cashOut(transfer);
    }

    private int getClubMemberCount(String accountId) {
        return clubMembersRepository.countMembersByClubAndRole(accountId, MEMBER);
    }

    private MoaClub getClubByAccId(String accId) {
        return moaClubRepository.findById(accId)
                .orElseThrow(() -> new BaseException(MOACLUB_NOT_FOUND));
    }

    private MoaClubMembers getClubMemberByClubAndUserName(MoaClub moaClub, String userName) {
        return clubMembersRepository.findByClubAndUserName(moaClub, userName)
                .orElseThrow(() -> new BaseException(MOACLUB_MEMBER_NOT_FOUND));
    }

    private void notificateWithdrawRequest(WithdrawReq request, String action) {
        MoaClub moaClub = getClubByAccId(request.getAccountId());
        List<User> clubMembers = getClubMemberUser(moaClub);

        String content = "[모아클럽📣]\n'" + moaClub.getName() + "'의 " + action;
        String url = "/moaclub/main/" + moaClub.getAccountId();

        for (User user : clubMembers) {
            notificationService.send(user, content, url, ALARM);
        }
    }

    private List<User> getClubMemberUser(MoaClub moaClub) {
        List<User> clubMembers = new ArrayList<>();

        for (MoaClubMembers member : moaClub.getClubMemberList()) {
            if (member.getRole() != NONMEMBER) {
                clubMembers.add(member.getUser());
            }
        }

        return clubMembers;
    }

    private String getCurrencyValue(String accId) {
        Currency currency = getClubByAccId(accId).getCurrency();
        if (currency == KRW) {
            return "원";
        } else if (currency == JPY) {
            return "엔";
        } else if (currency == CNY) {
            return "위안";
        } else {
            return "달러";
        }
    }
}
