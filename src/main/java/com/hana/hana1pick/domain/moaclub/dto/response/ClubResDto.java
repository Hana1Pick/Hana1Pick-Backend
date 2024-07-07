package com.hana.hana1pick.domain.moaclub.dto.response;

import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ClubResDto {

    private String name;
    private String accountId;
    private Long balance;
    private Long clubFee;
    private int atDate;
    private Currency currency;
    private LocalDate createDate;
    private List<MoaClubMember> memberList;
    private Long chatRoomId;

    public static ClubResDto of(MoaClub moaClub, List<MoaClubMember> memberList, Long chatRoomId) {
        return ClubResDto.builder()
                .name(moaClub.getName())
                .accountId(moaClub.getAccountId())
                .balance(moaClub.getBalance())
                .clubFee(moaClub.getClubFee())
                .atDate(moaClub.getAtDate())
                .currency(moaClub.getCurrency())
                .createDate(moaClub.getCreateDate())
                .memberList(memberList)
                .chatRoomId(chatRoomId)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoaClubMember {
        private String userName;
        private UUID userIdx;
        private String profile;
        private MoaClubMemberRole role;

        public static MoaClubMember from(MoaClubMembers moaClubMembers) {
            return new MoaClubMember(moaClubMembers.getUserName(), moaClubMembers.getUser().getIdx(), moaClubMembers.getUser().getProfile(), moaClubMembers.getRole());
        }
    }
}
