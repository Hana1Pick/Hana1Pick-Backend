package com.hana.hana1pick.domain.moaclub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.hana1pick.domain.moaclub.dto.response.ManagerChangeReq;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubMembersRepository;
import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole.MANAGER;
import static com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole.MEMBER;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerChangeListener implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MoaClubRepository moaClubRepository;
    private final MoaClubMembersRepository clubMembersRepository;

    private static final String MANAGER_CHANGE_KEY_PREFIX = "managerChangeRequest:";

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

            ManagerChangeReq request = objectMapper.readValue(pubMessage, ManagerChangeReq.class);

            // Redis key 설정
            String key = MANAGER_CHANGE_KEY_PREFIX + request.getAccountId();

            int memberCount = getClubMemberCount(request.getAccountId());

            if (request.getVotes().containsValue(false)) {
                redisTemplate.delete(key);

                // 클럽멤버 모두에게 관리자 변경 요청 취소 알림 - 추후 개발 예정
            } else if (request.getVotes().size() == memberCount && !request.getVotes().containsValue(false)) {
                // 관리자 변경
                MoaClub moaClub = getClubByAccId(request.getAccountId());
                MoaClubMembers memberUser = getClubMemberByClubAndUserName(moaClub, request.getUserName());
                MoaClubMembers memberCandidate = getClubMemberByClubAndUserName(moaClub, request.getCandidateName());

                clubMembersRepository.save(memberUser.updateUserRole(MEMBER));
                clubMembersRepository.save(memberCandidate.updateUserRole(MANAGER));

                redisTemplate.delete(key);

                // 클럽멤버 모두에게 관리자 변경 알림 - 추후 개발 예정
            }

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private MoaClub getClubByAccId(String accId) {
        return moaClubRepository.findById(accId)
                .orElseThrow(() -> new BaseException(MOACLUB_NOT_FOUND));
    }

    private MoaClubMembers getClubMemberByClubAndUserName(MoaClub moaClub, String userName) {
        return clubMembersRepository.findByClubAndUserName(moaClub, userName)
                .orElseThrow(() -> new BaseException(MOACLUB_MEMBER_NOT_FOUND));
    }

    private int getClubMemberCount(String accountId) {
        return clubMembersRepository.countMembersByClubAndRole(accountId, MEMBER);
    }
}
