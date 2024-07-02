package com.hana.hana1pick.domain.celublog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlterationReqDto {
    private String accountId;
    private String field;
    private MultipartFile srcImg;
    private String name;
}
