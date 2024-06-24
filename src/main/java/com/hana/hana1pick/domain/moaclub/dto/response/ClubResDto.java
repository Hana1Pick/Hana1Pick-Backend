package com.hana.hana1pick.domain.moaclub.dto.response;

import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import com.hana.hana1pick.domain.user.entity.User;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ClubResDto {

    private String accountId;
    private Long balance;
    private Long clubFee;
    private int atDate;
    private Currency currency;
    private List<MoaClubMember> memberList;

    public static ClubResDto of(MoaClub moaClub, List<MoaClubMember> memberList) {
        return ClubResDto.builder()
                .accountId(moaClub.getAccountId())
                .balance(moaClub.getBalance())
                .clubFee(moaClub.getClubFee())
                .atDate(moaClub.getAtDate())
                .currency(moaClub.getCurrency())
                .memberList(memberList)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoaClubMember {
        private String userName;
        private String profile;
        private MoaClubMemberRole role;

        public static MoaClubMember from(MoaClubMembers moaClubMembers) {
            return new MoaClubMember(moaClubMembers.getUserName(), moaClubMembers.getUser().getProfile(), moaClubMembers.getRole());
        }
    }
}
