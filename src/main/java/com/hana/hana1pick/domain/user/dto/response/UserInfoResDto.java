package com.hana.hana1pick.domain.user.dto.response;

import com.hana.hana1pick.domain.user.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserInfoResDto {
    private UUID userIdx;
    private LocalDate birth;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String profile;
    private String password;
    private String nation;

    public static UserInfoResDto from(User user) {
        return UserInfoResDto.builder()
                .birth(user.getBirth())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profile(user.getProfile())
                .nation(user.getNation().toString())
                .build();
    }
}
