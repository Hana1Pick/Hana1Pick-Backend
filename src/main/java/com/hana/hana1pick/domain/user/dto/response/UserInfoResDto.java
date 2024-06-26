package com.hana.hana1pick.domain.user.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class UserInfoResDto {
    private LocalDateTime birth;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String profile;

}
