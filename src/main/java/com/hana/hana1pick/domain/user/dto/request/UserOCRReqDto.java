package com.hana.hana1pick.domain.user.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOCRReqDto {
    public MultipartFile image;
}