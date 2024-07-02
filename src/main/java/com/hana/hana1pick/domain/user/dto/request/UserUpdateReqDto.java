package com.hana.hana1pick.domain.user.dto.request;

import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.entity.UserNation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserUpdateReqDto {
    private String name; // 이름
    private String email; //이메일
    private UserNation nation; //국적
    private LocalDate birth; //생년월일
    private String phone; //전화번호
    private String address; //주소
    private String password; //비밀번호
}
