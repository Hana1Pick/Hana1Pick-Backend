package com.hana.hana1pick.domain.user.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserInfoResDto {
    private UUID userIdx;
    private LocalDateTime birth;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String profile;
    private String password;
}
